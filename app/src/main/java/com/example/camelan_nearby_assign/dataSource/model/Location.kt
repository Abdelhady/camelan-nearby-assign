package com.example.camelan_nearby_assign.dataSource.model

data class Location(
    val address: String,
    val cc: String,
    val city: String,
    val country: String,
    val distance: Int,
    val formattedAddress: List<String>,
    val labeledLatLngs: List<LabeledLatLng>,
    val lat: Double,
    val lng: Double,
    val state: String
)