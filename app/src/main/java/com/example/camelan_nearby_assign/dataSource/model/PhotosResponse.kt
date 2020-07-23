package com.example.camelan_nearby_assign.dataSource.model

import com.example.camelan_nearby_assign.dataSource.model.photos.Meta
import com.example.camelan_nearby_assign.dataSource.model.photos.Response

data class PhotosResponse(
    val meta: Meta,
    val response: Response
)