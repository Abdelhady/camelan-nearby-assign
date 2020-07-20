package com.example.camelan_nearby_assign

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.example.camelan_nearby_assign.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewmodel = mainViewModel
        binding.lifecycleOwner = this
        permissionButton.setOnClickListener {
            // TODO ask for permission first before showing the fragment
            mainViewModel.hasPermission.value = true
        }
        mainViewModel.hasPermission.observe(this) {
            if (it) {
                loadNearbyFragment()
            }
        }
    }

    private fun loadNearbyFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, NearbyLocationsFragment())
            .commitAllowingStateLoss()
    }

}