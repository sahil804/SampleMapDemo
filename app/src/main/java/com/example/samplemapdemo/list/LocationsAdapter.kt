package com.example.samplemapdemo.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.samplemapdemo.R
import com.example.samplemapdemo.data.model.LocationInfo
import com.example.samplemapdemo.list.LocationListFragment.Companion.UNKNOWN_LOCATION_DISTANCE
import kotlinx.android.synthetic.main.layout_item_location.view.*

class LocationsAdapter(
    var locationsInfo: List<LocationInfo>,
    var itemSelected: (LocationInfo) -> Unit
) :
    RecyclerView.Adapter<LocationsAdapter.LocationViewHolder>() {

    inner class LocationViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val view: View = v
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder =
        LocationViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_location, parent, false)
        )

    override fun getItemCount() = locationsInfo.size

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        with(holder.itemView) {
            titleLocation.text = locationsInfo[position].name
            latitudeLongitude.text = resources.getString(
                R.string.lat_lng,
                locationsInfo[position].lat,
                locationsInfo[position].lng
            )
            if (locationsInfo[position].distanceLocal == UNKNOWN_LOCATION_DISTANCE) locationDistance.visibility =
                View.GONE
            locationDistance.text = resources.getQuantityString(
                R.plurals.distance_kms,
                locationsInfo[position].distanceLocal.toInt(), locationsInfo[position].distanceLocal
            )
            setOnClickListener {
                itemSelected(locationsInfo[position])
            }
        }
    }

    fun updateLocationList(newLocationsInfo: List<LocationInfo>) {
        locationsInfo = newLocationsInfo
        notifyDataSetChanged()
    }
}