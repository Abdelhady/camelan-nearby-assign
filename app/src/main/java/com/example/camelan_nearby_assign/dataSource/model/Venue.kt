package com.example.camelan_nearby_assign.dataSource.model

data class Venue(
    val categories: List<Category>,
    val id: String,
    val location: Location,
    val name: String,
    val photos: Photos
)