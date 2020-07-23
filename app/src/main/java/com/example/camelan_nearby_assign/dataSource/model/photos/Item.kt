package com.example.camelan_nearby_assign.dataSource.model.photos

data class Item(
    val checkin: Checkin,
    val createdAt: Int,
    val height: Int,
    val id: String,
    val prefix: String,
    val source: Source,
    val suffix: String,
    val user: User,
    val visibility: String,
    val width: Int
)