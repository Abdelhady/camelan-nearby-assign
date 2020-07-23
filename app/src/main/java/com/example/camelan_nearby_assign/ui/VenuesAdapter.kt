package com.example.camelan_nearby_assign.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.camelan_nearby_assign.MyApp
import com.example.camelan_nearby_assign.R
import com.example.camelan_nearby_assign.databinding.VenueItemBinding
import com.example.camelan_nearby_assign.services.PhotosService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.venue_item.view.*
import timber.log.Timber
import javax.inject.Inject

/**
 * https://www.untitledkingdom.com/blog/refactoring-recyclerview-adapter-to-data-binding-5631f239095f-0
 * also: https://androidwave.com/android-data-binding-recyclerview/
 */
class VenuesAdapter() : RecyclerView.Adapter<VenuesAdapter.ViewHolder>() {

    lateinit var myApp: MyApp // For di
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
        val viewHolder = ViewHolder(binding)
        myApp.appComponent.inject(viewHolder)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(private val binding: VenueItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @Inject
        lateinit var photosService: PhotosService

        fun bind(item: VenueItem) {
            binding.venue = item
            binding.executePendingBindings()
            Timber.d("location: applying binding for item with id: ${item.id}, & name: ${item.name}")
            photosService.getVenuePhotoUrl(item.id) { photoUrl ->
                Timber.d("location: getting venues photo, and url is: $photoUrl")
                Picasso.get()
                    .load(photoUrl)
                    .placeholder(R.drawable.photo_placeholder)
                    .into(itemView.photoImageView)
            }
        }

    }

}