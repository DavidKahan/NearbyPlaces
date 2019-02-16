package com.kahan.david.nearbyplaces.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kahan.david.nearbyplaces.model.Place


/**
 * Created by david on 15/02/2019.
 */
class PlacesListAdapter( private val placesList: List<Place>): RecyclerView.Adapter<PlacesListAdapter.PlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.kahan.david.nearbyplaces.R.layout.places_list_row_item, parent, false)
        return PlaceViewHolder(view)
    }


//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
//        val binding = DataBindingUtil.inflate<com.kahan.david.nearbyplaces.databinding.PlacesListRowItemBinding>(
//            LayoutInflater.from(parent?.context),
//            com.kahan.david.nearbyplaces.R.layout.places_list_row_item, parent, false)
//        return PlacesViewHolder(binding)
//    }

    override fun getItemCount() = placesList.size

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = placesList[position]
        holder.titleTV.text = place.name
        holder.addressTV.text = place.address
//        val openText : String
//
//        openText = if (place.openingHours?.isOpen!!){
//            "Open Now"
//        } else {
//            "Closed Now"
//        }
//        holder.currentlyOpenTV.text = openText
        holder.ratingTV.text = "Rating: " +place.rating.toString()
    }

    class PlaceViewHolder(placeItem : View) : RecyclerView.ViewHolder(placeItem) {
        var titleTV: TextView
        var addressTV: TextView
        var currentlyOpenTV: TextView
        var ratingTV: TextView
        init {
            placeItem.isClickable = false
            titleTV = placeItem.findViewById(com.kahan.david.nearbyplaces.R.id.title)
            addressTV = placeItem.findViewById(com.kahan.david.nearbyplaces.R.id.address)
            currentlyOpenTV = placeItem.findViewById(com.kahan.david.nearbyplaces.R.id.currently_open)
            ratingTV = placeItem.findViewById(com.kahan.david.nearbyplaces.R.id.rating)
        }
    }
}