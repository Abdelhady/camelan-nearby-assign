package com.example.camelan_nearby_assign.dataSource

import com.example.camelan_nearby_assign.dataSource.model.ExploreResponse
import com.example.camelan_nearby_assign.dataSource.model.PhotosResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

public interface FoursquareVenuesService {

    @GET("explore")
    fun getNearbyVenues(
        @Query("ll") ll: String,
        @Query("radius") radius: Int
    ): Call<ExploreResponse>

    @GET("{venueId}/photos")
    fun getVenuePhotos(@Path("venueId") venueId: String): Call<PhotosResponse>

}