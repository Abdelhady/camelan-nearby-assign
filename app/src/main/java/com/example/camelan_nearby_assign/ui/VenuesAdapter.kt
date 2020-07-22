package com.example.camelan_nearby_assign.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.camelan_nearby_assign.databinding.VenueItemBinding
import timber.log.Timber

/**
 * https://www.untitledkingdom.com/blog/refactoring-recyclerview-adapter-to-data-binding-5631f239095f-0
 * also: https://androidwave.com/android-data-binding-recyclerview/
 */
class VenuesAdapter() : RecyclerView.Adapter<VenuesAdapter.ViewHolder>() {

    var items: MutableList<VenueItem> = mutableListOf()
        set(value) {
            value.sortBy { it.name }
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = VenueItemBinding.inflate(
            inflater,
            parent,
            false
        ) // To fill the full width: https://stackoverflow.com/a/30692398/905801
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(val binding: VenueItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VenueItem) {
            binding.venue = item
            binding.executePendingBindings()
            Timber.d("location: applying binding form item: ${item.name}")
        }

    }

}