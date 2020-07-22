package com.example.camelan_nearby_assign.di

import com.example.camelan_nearby_assign.dataSource.NetworkModule
import com.example.camelan_nearby_assign.screens.nearbyLocations.di.NearbyLocationsComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        SubcomponentsModule::class,
        NetworkModule::class
    ]
)
interface ApplicationComponent {

    fun nearbyLocationsComponent(): NearbyLocationsComponent.Factory

}