package com.example.camelan_nearby_assign.dataSource.model.explore

import com.example.camelan_nearby_assign.dataSource.model.explore.Category
import com.example.camelan_nearby_assign.dataSource.model.explore.Location
import com.example.camelan_nearby_assign.dataSource.model.explore.Photos

data class Venue(
    val categories: List<Category>,
    val id: String,
    val location: Location,
    val name: String,
    val photos: Photos
)