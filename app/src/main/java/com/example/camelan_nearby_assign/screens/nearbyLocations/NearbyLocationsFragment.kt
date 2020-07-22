package com.example.camelan_nearby_assign.screens.nearbyLocations

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.camelan_nearby_assign.MyApp
import com.example.camelan_nearby_assign.R
import com.example.camelan_nearby_assign.ui.VenuesAdapter
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.nearby_locations_fragment.*
import timber.log.Timber

class NearbyLocationsFragment : Fragment() {

    private var requestingLocationUpdates = true // TODO update this as per user's preference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var currentLocation: Location
    private val adapter = VenuesAdapter()

    companion object {
        fun newInstance() =
            NearbyLocationsFragment()
    }

    val viewModel by activityViewModels<NearbyLocationsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.nearby_locations_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
        (activity?.application as MyApp).appComponent
            .nearbyLocationsComponent()
            .create()
            .inject(viewModel)

        fetchLastKnownLocation() // TODO most of the time, it returns null, is it worth using after all !
        initLocationListener()
        initVenuesList()
    }

    private fun initVenuesList() {
        venuesRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        venuesRecyclerView.adapter = adapter
        viewModel.venueItems.observe(requireActivity()) {
            Timber.d("location: new venues has arrived to fragment, with size: ${it.size}")
            adapter.items = it.toMutableList()
        }
    }

    private fun initLocationListener() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                Timber.d("locations found are ${locationResult.locations.size} locations")
                if (locationResult.locations.size == 0) return
                currentLocation = locationResult.locations[0]
                Timber.d("current location details .. Longitude: ${currentLocation.longitude}, Latitude: ${currentLocation.latitude}")
                // TODO refresh nearby locations list here
                viewModel.refreshPlaces(currentLocation.latitude, currentLocation.longitude)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // TODO if last known location was null, we still need to get location updates at least once!
        if (requestingLocationUpdates) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // This shouldn't be reached, because activity is responsible for requesting the permission
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

    private fun fetchLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // This shouldn't be reached, because activity is responsible for requesting the permission
            Timber.d("location permission not exists")
            return
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    lastLocation =
                        location // TODO should be added as a live data in the view model?
                    Timber.d("last location details .. Longitude: ${location.longitude}, Latitude: ${location.latitude}")
                } else {
                    Timber.d("location is null")
                    // This means that user didn't use any app that asks for location (like maps)
                    // since last time the user enabled device's location setting
                }
            }
    }

}