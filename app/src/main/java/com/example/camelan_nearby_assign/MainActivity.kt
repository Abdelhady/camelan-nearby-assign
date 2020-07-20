package com.example.camelan_nearby_assign

import android.Manifest
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
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                mainViewModel.hasPermission.value = true
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
        checkLocationPermission()
        mainViewModel.hasPermission.observe(this) {
            if (it) {
                loadNearbyFragment()
            }
        }
    }

    private fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Permissions already granted while installing from Google Play
            mainViewModel.hasPermission.value = true
            return
        }
        // https://developer.android.com/training/permissions/requesting#allow-system-manage-request-code
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                mainViewModel.hasPermission.value = true
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

    private fun loadNearbyFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, NearbyLocationsFragment())
            .commitAllowingStateLoss()
    }

}