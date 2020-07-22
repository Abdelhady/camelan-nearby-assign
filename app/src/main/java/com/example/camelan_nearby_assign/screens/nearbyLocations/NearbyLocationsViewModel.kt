package com.example.camelan_nearby_assign.screens.nearbyLocations

import androidx.lifecycle.ViewModel
import com.example.camelan_nearby_assign.repos.VenuesRepo
import javax.inject.Inject

class NearbyLocationsViewModel : ViewModel() {

    @Inject
    lateinit var venuesRepo: VenuesRepo

    fun refreshPlaces(latitude: Double, longitude: Double) {
        // TODO handle returned value to be displayed in a recycler view
        venuesRepo.getNearbyVenues(latitude, longitude, 500)
    }

}