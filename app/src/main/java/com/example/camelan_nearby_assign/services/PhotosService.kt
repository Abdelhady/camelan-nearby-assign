package com.example.camelan_nearby_assign.services

import com.example.camelan_nearby_assign.dataSource.FoursquareVenuesService
import javax.inject.Inject

class PhotosService @Inject constructor(
    private var venuesService: FoursquareVenuesService
) {

    fun getVenuePhotoUrl(venueId: String, successCallback: (String) -> Unit) {
        // TODO get the real venue photo from venue's APIs
        successCallback("https://fastly.4sqi.net/img/general/720x960/47876605_x9Qvya2W8LddoiblHvcymzR-A6cjpVgWZSekvGIzhhM.jpg")
    }

}