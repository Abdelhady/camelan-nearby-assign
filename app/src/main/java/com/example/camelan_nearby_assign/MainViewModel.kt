package com.example.camelan_nearby_assign

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    var hasPermission = MutableLiveData<Boolean>(false)

}