package com.example.camelan_nearby_assign.dataSource.model

import com.example.camelan_nearby_assign.dataSource.model.explore.Meta
import com.example.camelan_nearby_assign.dataSource.model.explore.Response

/**
 * Created from a sample JSON response, like how it is mentioned here:
 * https://dev.to/bensalcie/android-kotlin-get-data-from-restful-api-having-multiple-json-objects-o5a
 */
data class ExploreResponse(
    val meta: Meta,
    val response: Response
)