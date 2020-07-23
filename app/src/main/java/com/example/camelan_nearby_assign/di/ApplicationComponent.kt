package com.example.camelan_nearby_assign.di

import com.example.camelan_nearby_assign.dataSource.NetworkModule
import com.example.camelan_nearby_assign.screens.nearbyLocations.di.NearbyLocationsComponent
import com.example.camelan_nearby_assign.ui.VenuesAdapter
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

    fun inject(venuesViewHolder: VenuesAdapter.ViewHolder)

    fun nearbyLocationsComponent(): NearbyLocationsComponent.Factory

}