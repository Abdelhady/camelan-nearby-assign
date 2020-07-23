package com.example.camelan_nearby_assign.screens.nearbyLocations

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.camelan_nearby_assign.dataSource.model.explore.Venue
import com.example.camelan_nearby_assign.repos.VenuesRepo
import com.example.camelan_nearby_assign.ui.VenueItem
import javax.inject.Inject

class NearbyLocationsViewModel : ViewModel() {

    @Inject
    lateinit var venuesRepo: VenuesRepo

    // Initiating with `emptyList()` to initiate `isEmpty()` as early as possible too
    private val venues = MutableLiveData<List<Venue>>(emptyList())
    val venueItems = MediatorLiveData<List<VenueItem>>().apply {
        addSource(venues) {
            val items = arrayListOf<VenueItem>()
            for (venue in it) {
                val category = venue.categories?.get(0)?.name + " - "
                val address = venue.location.formattedAddress.joinToString(", ")
                items.add(VenueItem(venue.id, venue.name, category + address, ""))
            }
            value = items.toList()
        }
    }
    val isEmpty = MediatorLiveData<Boolean>().apply {
        addSource(venueItems) {
            value = if (venueItems.value == null) {
                true
            } else {
                venueItems.value!!.isEmpty()
            }
        }
    }
    val loading = MutableLiveData(true)
    val hasError = MutableLiveData(false)


    fun refreshPlaces(latitude: Double, longitude: Double) {
        loading.value = true
        hasError.value = false
        venuesRepo.getNearbyVenues(
            latitude,
            longitude,
            5000,
            successCallback = {
                venues.value = it
                loading.value = false
                hasError.value = false
            },
            errorCallback = {
                loading.value = false
                hasError.value = true
            })
    }

}