package com.example.samplemapdemo.list

import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.samplemapdemo.R
import com.example.samplemapdemo.data.model.LocationInfo
import com.example.samplemapdemo.data.model.Status
import com.example.samplemapdemo.detail.LocationDetailFragmentArgs
import com.example.samplemapdemo.di.DaggerViewModelFactory
import com.example.samplemapdemo.main.AddMarkerDialogFragment
import com.example.samplemapdemo.main.BaseFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_content_bottom_sheet.*
import javax.inject.Inject

class LocationListFragment : BaseFragment() {

    companion object {
        const val UNKNOWN_LOCATION_DISTANCE = -1L
        const val EMPTY = ""
        const val UNKNOWN_LOCATION = "UNKNOWN LOCATION"
        const val ONE_KM = 1000
    }

    lateinit var locationsAdapter: LocationsAdapter
    lateinit var mainActivityViewModel: MainActivityViewModel

    @Inject
    lateinit var viewModelFactory: DaggerViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)
                .get(MainActivityViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        mainActivityViewModel.locations.observe(viewLifecycleOwner, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    dataView.visibility = View.VISIBLE
                    errorView.visibility = View.GONE
                    result.data?.let {
                        locationsAdapter.updateLocationList(getSortedList(it))
                        addMarkers(it)
                    }
                }
                Status.ERROR -> {
                    progressBar.visibility = View.GONE
                    dataView.visibility = View.GONE
                    errorView.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    dataView.visibility = View.GONE
                    errorView.visibility = View.GONE
                }
            }
        })
        retryButton.setOnClickListener {
            mainActivityViewModel.getLocations()
        }
        checkDialogRecreation()
    }

    private fun getSortedList(locationsInfo: List<LocationInfo>): List<LocationInfo> {
        val sortedList = mutableListOf<LocationInfo>()
        locationsInfo.forEach {
            it.distanceLocal = getDistance(it)
            sortedList.add(it)
        }
        sortedList.sortBy { it.distanceLocal }
        return sortedList
    }

    private fun addMarkers(locationsList: List<LocationInfo>) {
        val boundsBuilder = LatLngBounds.Builder()
        map?.run {
            if (isViewReady && isMapReady) {
                clear()
                locationsList.forEachIndexed { index, location ->
                    addMarker(
                        MarkerOptions()
                            .position(LatLng(location.lat, location.lng))
                            .title(location.name)
                            .icon(BitmapDescriptorFactory.defaultMarker())
                    ).tag = index
                    boundsBuilder.include(LatLng(location.lat, location.lng))
                }
                moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 200))
            }
        }
    }

    private fun setUpRecyclerView() {
        locationsAdapter = LocationsAdapter(
            listOf(), ::itemSelected
        )
        rvLocation.apply {
            setHasFixedSize(true)
            adapter = locationsAdapter
        }
    }

    private fun itemSelected(locationInfo: LocationInfo) {
        navHostFragment.findNavController().navigate(
            R.id.locationDetailFragment,
            LocationDetailFragmentArgs(locationInfo).toBundle()
        )
    }

    override fun getContentLayoutId() =
        R.layout.fragment_map_content

    override fun getBottomSheetResourceId() =
        R.layout.layout_content_bottom_sheet

    override fun onMapReady(googleMap: GoogleMap?) {
        super.onMapReady(googleMap)
        googleMap?.run {
            setOnMapLongClickListener { point ->
                handleMapLongClick(
                    LocationInfo(
                        getAddress(point),
                        point.latitude,
                        point.longitude,
                        EMPTY,
                        true, UNKNOWN_LOCATION_DISTANCE
                    )
                )
            }

            setOnMarkerClickListener { marker -> markerClicked(marker) }
        }
        mainActivityViewModel.getLocations()
    }

    private fun handleMapLongClick(locationInfo: LocationInfo) {
        val dialogFragment =
            AddMarkerDialogFragment.newInstance(locationInfo)
        mainActivityViewModel.toAddLocation = locationInfo
        dialogFragment.onDismissListener = ::dismissDialog
        dialogFragment.onSaveListener = {
            mainActivityViewModel.isDialogVisible = false
            addLocation(it)
        }
        dialogFragment.show(requireActivity().supportFragmentManager, "dialogFragment")
        mainActivityViewModel.isDialogVisible = true
    }

    private fun dismissDialog() {
        mainActivityViewModel.isDialogVisible = false
    }

    private fun checkDialogRecreation() {
        with(requireActivity().supportFragmentManager) {
            findFragmentByTag("dialogFragment")?.let {
                (it as DialogFragment).dismiss()
                if (mainActivityViewModel.isDialogVisible) {
                    mainActivityViewModel.toAddLocation?.let {
                        handleMapLongClick(it)
                    }
                }
            }
        }
    }

    private fun getDistance(locationInfo: LocationInfo): Long {
        val serviceLocation = Location("")
        serviceLocation.latitude = locationInfo.lat
        serviceLocation.longitude = locationInfo.lng
        return currentLocation?.distanceTo(serviceLocation)?.toLong()?.let {
            it / ONE_KM
        } ?: UNKNOWN_LOCATION_DISTANCE
    }

    private fun getAddress(latLng: LatLng): String =
        geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            .takeIf { it.isNotEmpty() }?.get(0)?.let {
                it.locality ?: it.subLocality ?: it.adminArea ?: UNKNOWN_LOCATION
            } ?: UNKNOWN_LOCATION

    private fun addLocation(locationInfo: LocationInfo) {
        mainActivityViewModel.addLocation(locationInfo)
    }

    private fun markerClicked(marker: Marker): Boolean {
        marker.tag?.let {
            val locationInfo = mainActivityViewModel.locations.value?.data?.get(it as Int)
            locationInfo?.run { itemSelected(this) }
        }
        return true
    }
}