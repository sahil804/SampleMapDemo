package com.example.samplemapdemo.detail

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.samplemapdemo.R
import com.example.samplemapdemo.data.model.LocationInfo
import com.example.samplemapdemo.di.DaggerViewModelFactory
import com.example.samplemapdemo.list.MainActivityViewModel
import com.example.samplemapdemo.main.BaseFragment
import com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.layout_location_info_bottom_sheet.*
import javax.inject.Inject

class LocationDetailFragment : BaseFragment() {

    companion object {
        const val DEFAULT_DETAIL_ZOOM = 10f
    }

    @Inject
    lateinit var viewModelFactory: DaggerViewModelFactory

    lateinit var mainActivityViewModel: MainActivityViewModel
    lateinit var locationInfo: LocationInfo

    override fun getBottomSheetResourceId() =
        R.layout.layout_location_info_bottom_sheet

    override fun getContentLayoutId() =
        R.layout.fragment_map_content

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)
                .get(MainActivityViewModel::class.java)
        arguments?.let {
            locationInfo = LocationDetailFragmentArgs.fromBundle(it).locationInfo
        }
        initViews()
    }

    private fun initViews() {
        with(locationInfo) {
            description?.let {
                locationDescription.setText(it)
            }
            btSave.isEnabled = userAddedLocation
            locationDescription.isEnabled = userAddedLocation
            btSave.setOnClickListener {
                locationDescription.text.toString().takeIf { it.isNotEmpty() }?.let {
                    description = it
                    mainActivityViewModel.updateDescription(this)
                }
                findNavController().popBackStack()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        super.onMapReady(googleMap)
        googleMap?.run {
            addMarker(this)
        }
    }

    private fun addMarker(googleMap: GoogleMap) {
        with(googleMap) {
            if (isViewReady && isMapReady) {
                clear()
                addMarker(
                    MarkerOptions()
                        .position(LatLng(locationInfo.lat, locationInfo.lng))
                        .title(locationInfo.name)
                        .icon(BitmapDescriptorFactory.defaultMarker())
                )
            }
            animateCamera(
                newLatLngZoom(
                    LatLng(locationInfo.lat, locationInfo.lng),
                    DEFAULT_DETAIL_ZOOM
                )
            )
        }
    }
}