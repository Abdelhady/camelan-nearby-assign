package com.example.camelan_nearby_assign.screens.nearbyLocations

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.camelan_nearby_assign.MyApp
import com.example.camelan_nearby_assign.databinding.NearbyLocationsFragmentBinding
import com.example.camelan_nearby_assign.ui.VenuesAdapter
import kotlinx.android.synthetic.main.nearby_locations_fragment.*
import timber.log.Timber

class NearbyLocationsFragment : Fragment() {

    private lateinit var lastLocation: Location
    private val adapter = VenuesAdapter()

    companion object {
        fun newInstance() =
            NearbyLocationsFragment()
    }

    val viewModel by activityViewModels<NearbyLocationsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: NearbyLocationsFragmentBinding =
            NearbyLocationsFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity?.application as MyApp).appComponent
            .nearbyLocationsComponent()
            .create()
            .inject(viewModel)

        initVenuesList()
    }

    private fun initVenuesList() {
        venuesRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        venuesRecyclerView.adapter = adapter
        adapter.myApp = activity?.application as MyApp // For di injection
        venuesRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                LinearLayoutManager.VERTICAL
            )
        ) // Showing a divider line: https://stackoverflow.com/a/54792091/905801
        viewModel.venueItems.observe(requireActivity()) {
            Timber.d("location: new venues has arrived to fragment, with size: ${it.size}")
            adapter.items = it.toMutableList()
        }
    }

}