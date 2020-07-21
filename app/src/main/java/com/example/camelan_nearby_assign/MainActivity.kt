package com.example.camelan_nearby_assign

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.example.camelan_nearby_assign.databinding.ActivityMainBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel>()
    private val REQUEST_CHECK_SETTINGS: Int = 1
    private var settingsDialogIsVisible = false

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                markPermissionEnabled()
            } else {
                Toast.makeText(
                    this,
                    R.string.location_permission_declined_message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewmodel = mainViewModel
        binding.lifecycleOwner = this
        if (!mainViewModel.hasPermission.value!!) {
            checkLocationPermission()
        }
        mainViewModel.locationSettingEnabled.observe(this) {
            if (it) {
                loadNearbyFragment()
            }
        }
        settingsButton.setOnClickListener {
            ensureLocationSettingsEnabled()
        }
    }

    private fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Permissions already granted while installing from Google Play
            markPermissionEnabled()
            return
        }
        // https://developer.android.com/training/permissions/requesting#allow-system-manage-request-code
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                markPermissionEnabled()
            }
            else -> {
                permissionButton.setOnClickListener {
                    requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
            }
        }
    }

    private fun markPermissionEnabled() {
        mainViewModel.hasPermission.value = true
        ensureLocationSettingsEnabled()
    }

    private fun loadNearbyFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, NearbyLocationsFragment())
            .commitAllowingStateLoss()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Timber.d("location settings resolution: user enabled it")
                        mainViewModel.locationSettingEnabled.value = true
                        settingsDialogIsVisible = false
                    }
                    Activity.RESULT_CANCELED -> {
                        Timber.d("location settings resolution: user refused it")
                        Toast.makeText(
                            this,
                            "Enable location settings first please",
                            Toast.LENGTH_LONG
                        ).show()
                        settingsDialogIsVisible = false
                    }
                }
            }
        }
    }

    private fun ensureLocationSettingsEnabled() {
        if (settingsDialogIsVisible) {
            // wait for user to respond to the opened one first
            return
        }
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            mainViewModel.locationSettingEnabled.value = true
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    Timber.d("location setting resolution is starting")
                    settingsDialogIsVisible = true
                    exception.startResolutionForResult(
                        this,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

}