package com.example.camelan_nearby_assign.screens.nearbyLocations

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.example.camelan_nearby_assign.MyApp
import com.example.camelan_nearby_assign.R
import com.example.camelan_nearby_assign.databinding.ActivityMainBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel>()
    private val nearbyFragmentViewModel by viewModels<NearbyLocationsViewModel>()
    private val REQUEST_CHECK_SETTINGS: Int = 1
    private var settingsDialogIsVisible = false
    private var isRealtime = true
    private val REALTIME_PREF_KEY = "realtime_pref_key"
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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
            DataBindingUtil.setContentView(
                this,
                R.layout.activity_main
            )
        binding.viewmodel = mainViewModel
        binding.lifecycleOwner = this
        readSavedRealtimePref()
        (application as MyApp).appComponent
            .nearbyLocationsComponent()
            .create()
            .inject(nearbyFragmentViewModel) // Injecting it here also, because activity may call it before using the fragment
        if (!mainViewModel.hasPermission.value!!) {// `hasPermission` could be populated before, in case device is being rotated
            checkLocationPermission()
        }
        initLocationSettingListeners()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLastKnownLocation()
        initLocationListener()
    }

    override fun onResume() {
        super.onResume()
        // TODO: Always listening to location updates is not efficient to device's battery when
        // user is choosing only `Single Update` mode, so consider this when user changes Realtime Preference
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Timber.d("location permission not exists")
            return
        }
        // TODO extract interval/fastestInterval as separate constants to be used here & in the activity
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun initLocationSettingListeners() {
        mainViewModel.locationSettingEnabled.observe(this) {
            if (it) {
                loadNearbyFragment()
            }
        }
        settingsButton.setOnClickListener {
            ensureLocationSettingsEnabled()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.nearby_menu, menu)
        updateRealtimeMenuItem(menu.findItem(R.id.realtime_option))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.realtime_option -> {
                toggleRealtimePreference(item)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleRealtimePreference(item: MenuItem) {
        isRealtime = !isRealtime
        updateRealtimeMenuItem(item)
        saveRealtimePref()
    }

    private fun updateRealtimeMenuItem(item: MenuItem) {
        item.title = if (isRealtime)
            getString(R.string.realtime)
        else
            getString(R.string.single_update)
    }

    /**
     * Saving activity level preference:
     * https://developer.android.com/training/data-storage/shared-preferences#WriteSharedPreference
     */
    private fun saveRealtimePref() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean(REALTIME_PREF_KEY, isRealtime)
            commit()
        }
    }

    private fun readSavedRealtimePref() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        isRealtime = sharedPref.getBoolean(REALTIME_PREF_KEY, true)
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
        val fragmentTag = "nearbyFrag"
        if (supportFragmentManager.findFragmentByTag(fragmentTag) != null) {
            return // fragment is already added (probably device is just being rotated right now)
        }
        supportFragmentManager.beginTransaction()
            .add(
                R.id.fragmentContainer,
                NearbyLocationsFragment(),
                fragmentTag
            )
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

    private fun initLocationListener() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                Timber.d("locations found are ${locationResult.locations.size} locations")
                if (locationResult.locations.size == 0) return
                updateCurrentLocationIfNeeded(locationResult.locations[0])
            }
        }
    }

    private fun updateCurrentLocationIfNeeded(newLocation: Location) {
        if (!isRealtime && currentLocation != null) {
            return
        }
        val distanceInMeters = currentLocation?.distanceTo(newLocation)
        Timber.d("distance to new location is: $distanceInMeters")
        if (distanceInMeters != null && distanceInMeters < 500) {// TODO extract this distance in a constants file or something
            return
        }
        currentLocation = newLocation
        Timber.d("current location details .. Longitude: ${currentLocation!!.longitude}, Latitude: ${currentLocation!!.latitude}")
        nearbyFragmentViewModel.refreshPlaces(
            currentLocation!!.latitude,
            currentLocation!!.longitude
        )
    }

    private fun fetchLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // This shouldn't be reached, because activity is responsible for requesting the permission
            Timber.d("location permission not exists")
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    updateCurrentLocationIfNeeded(location)
                    Timber.d("last location details .. Longitude: ${location.longitude}, Latitude: ${location.latitude}")
                } else {
                    Timber.d("location is null")
                    // This means that user didn't use any app that asks for location (like maps)
                    // since last time the user enabled device's location setting
                }
            }
    }

}