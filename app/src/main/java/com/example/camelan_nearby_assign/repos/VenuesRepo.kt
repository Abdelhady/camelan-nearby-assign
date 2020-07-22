package com.example.camelan_nearby_assign.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.camelan_nearby_assign.dataSource.FoursquareVenuesService
import com.example.camelan_nearby_assign.dataSource.model.ExploreResponse
import com.example.camelan_nearby_assign.dataSource.model.Venue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class VenuesRepo @Inject constructor(
    private val venuesService: FoursquareVenuesService
) {

    fun getNearbyVenues(latitude: Double, longitude: Double, radius: Int): LiveData<List<Venue>> {
        val data = MutableLiveData<List<Venue>>()
        val ll = "${latitude},${longitude}"
        venuesService.getNearbyVenues(ll, radius)
            .enqueue(object : Callback<ExploreResponse> {
                override fun onFailure(call: Call<ExploreResponse>, t: Throwable) {
                    Timber.d("location: getNearbyVenues onFailure")
                }

                override fun onResponse(
                    call: Call<ExploreResponse>,
                    response: Response<ExploreResponse>
                ) {
                    if (!response.isSuccessful) {
                        Timber.d("location: getNearbyVenues is not successful")
                        return
                    }
                    Timber.d("location: getNearbyVenues is successful")
                    val items = response.body()?.response?.groups?.get(0)?.items
                    var venues = arrayListOf<Venue>()
                    if (items != null) {
                        for (item in items){
                            venues.add(item.venue)
                            Timber.d("location: venue name: ${item.venue.name}")
                        }
                    }
                    data.value = venues.toList()
                }

            })
        return data
    }

}