package com.example.camelan_nearby_assign.di

import com.example.camelan_nearby_assign.screens.nearbyLocations.di.NearbyLocationsComponent
import dagger.Module

@Module(
    subcomponents = [
        NearbyLocationsComponent::class
    ]
)
class SubcomponentsModule {
}