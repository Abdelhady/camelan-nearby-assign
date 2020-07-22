package com.example.camelan_nearby_assign.screens.nearbyLocations.di

import com.example.camelan_nearby_assign.screens.nearbyLocations.MainViewModel
import com.example.camelan_nearby_assign.screens.nearbyLocations.NearbyLocationsViewModel
import dagger.Subcomponent

@Subcomponent
interface NearbyLocationsComponent {

    @Subcomponent.Factory
    interface Factory{
        fun create(): NearbyLocationsComponent
    }

    fun inject(viewModel: MainViewModel)

    fun inject(viewModel: NearbyLocationsViewModel)

}