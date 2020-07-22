package com.example.camelan_nearby_assign.screens.nearbyLocations

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    var hasPermission = MutableLiveData<Boolean>(false)
    var locationSettingEnabled = MutableLiveData<Boolean>(false)

}