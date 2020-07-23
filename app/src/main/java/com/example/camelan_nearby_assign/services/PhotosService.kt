package com.example.camelan_nearby_assign.services

import com.example.camelan_nearby_assign.dataSource.FoursquareVenuesService
import com.example.camelan_nearby_assign.dataSource.model.PhotosResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class PhotosService @Inject constructor(
    private var venuesService: FoursquareVenuesService
) {

    fun getVenuePhotoUrl(venueId: String, successCallback: (String) -> Unit) {
        // Uncomment these 2 lines for testing, not to utilize the whole 50 premium calls/day (photos call is premium)
//        successCallback("https://fastly.4sqi.net/img/general/width300/47876605_x9Qvya2W8LddoiblHvcymzR-A6cjpVgWZSekvGIzhhM.jpg")
//        return

        venuesService.getVenuePhotos(venueId)
            .enqueue(object : Callback<PhotosResponse> {
                override fun onFailure(call: Call<PhotosResponse>, t: Throwable) {
                    Timber.d("location: getVenuePhotoUrl failed")
                }

                override fun onResponse(
                    call: Call<PhotosResponse>,
                    response: Response<PhotosResponse>
                ) {
                    if (!response.isSuccessful) {
                        Timber.d("location: getVenuePhotoUrl response is not successful")
                        return
                    }
                    val items = response.body()?.response?.photos?.items
                    if (items?.size == 0) {
                        Timber.d("location: getVenuePhotoUrl, photos list is empty for venue with id: $venueId")
                        return
                    }
                    val item = items?.get(0)
                    if (item != null) {
                        // TODO check if 300px is a suitable size!
                        // Sizes are determined as per: https://developer.foursquare.com/docs/api-reference/venues/photos/
                        val size = "width300"
                        val url = item.prefix + size + item.suffix
                        successCallback(url)
                    }
                }

            })
    }

}