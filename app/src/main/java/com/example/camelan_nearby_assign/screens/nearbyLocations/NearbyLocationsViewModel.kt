package com.example.camelan_nearby_assign.screens.nearbyLocations

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.camelan_nearby_assign.dataSource.model.Venue
import com.example.camelan_nearby_assign.repos.VenuesRepo
import com.example.camelan_nearby_assign.ui.VenueItem
import javax.inject.Inject

class NearbyLocationsViewModel : ViewModel() {

    @Inject
    lateinit var venuesRepo: VenuesRepo

    val venues = MutableLiveData<List<Venue>>()
    val venueItems = MediatorLiveData<List<VenueItem>>().apply {
        addSource(venues) {
            val items = arrayListOf<VenueItem>()
            for (venue in it) {
                items.add(VenueItem(venue.name, "some address", "some photo url"))
            }
            value = items.toList()
        }
    }


    fun refreshPlaces(latitude: Double, longitude: Double) {
        // TODO handle returned value to be displayed in a recycler view
        venuesRepo.getNearbyVenues(latitude, longitude, 500) {
            venues.value = it
        }
    }

}