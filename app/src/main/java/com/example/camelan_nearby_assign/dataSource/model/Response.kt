package com.example.camelan_nearby_assign.dataSource.model

data class Response(
    val groups: List<Group>,
    val headerFullLocation: String,
    val headerLocation: String,
    val headerLocationGranularity: String,
    val suggestedBounds: SuggestedBounds,
    val totalResults: Int,
    val warning: Warning
)