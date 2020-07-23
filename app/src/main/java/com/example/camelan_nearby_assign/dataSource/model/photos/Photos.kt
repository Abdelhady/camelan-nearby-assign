package com.example.camelan_nearby_assign.dataSource.model.photos

data class Photos(
    val count: Int,
    val dupesRemoved: Int,
    val items: List<Item>
)