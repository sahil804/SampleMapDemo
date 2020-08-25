package com.example.samplemapdemo.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.example.samplemapdemo.R
import com.example.samplemapdemo.hasPermission
import com.example.samplemapdemo.isLocationEnabled
import com.example.samplemapdemo.requestPermissionWithRationale
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_base.*
import kotlinx.android.synthetic.main.fragment_base.view.*
import java.util.Locale

abstract class BaseFragment : DaggerFragment(), OnMapReadyCallback,
    ViewTreeObserver.OnGlobalLayoutListener {

    var map: GoogleMap? = null
    var mapView: View? = null
    var isViewReady = false
    var isMapReady = false
    var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    lateinit var geocoder: Geocoder

    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    var currentState = BottomSheetBehavior.STATE_HALF_EXPANDED

    companion object {
        const val DEFAULT_PEEK_HEIGHT = 65F
        const val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 37
        const val isFitToContent = false
        const val defaultHide = false
        const val HALF_EXPANDED_RATIO = 0.4f
        const val EXPANDED_OFFSET = 20
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (!context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestFineLocationPermission()
        }
    }

    private fun requestFineLocationPermission() {
        val permissionApproved =
            context?.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) ?: return

        if (permissionApproved) {
            Log.d("Fragment", "location is there")
        } else {
            requestPermissionWithRationale(
                Manifest.permission.ACCESS_FINE_LOCATION,
                REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE) {
            getCurrentLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setupLocation()
    }

    private fun setupLocation() {
        locationRequest = LocationRequest()
        locationRequest.interval = 60 * 1000
        locationRequest.fastestInterval = 1000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        settingsClient.checkLocationSettings(locationSettingsRequest)
    }

    override fun onStart() {
        super.onStart()
        getCurrentLocation()
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        with(requireContext()) {
            if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) && isLocationEnabled()) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.myLooper()
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val content = inflater.inflate(R.layout.fragment_base, container, false)
        content.bottomSheetLayout.addView(getBottom(root = container))
        content.contentContainer.addView(getLayout(root = container))
        return content
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
        initBottomSheet()
    }

    private fun initMap() {
        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().replace(R.id.map, mapFragment)
            .commit()
        mapView = mapFragment.view
        if (mapView?.width != 0 && mapView?.height != 0) {
            isViewReady = true
        } else {
            mapView?.viewTreeObserver?.addOnGlobalLayoutListener(this)
        }
        mapFragment.getMapAsync(this)
    }

    private fun getBottom(
        resourceId: Int = getBottomSheetResourceId(),
        root: ViewGroup? = null
    ): View {
        return LayoutInflater.from(requireContext())
            .inflate(resourceId, root, false)
    }

    private fun getLayout(resourceId: Int = getContentLayoutId(), root: ViewGroup? = null): View {
        return LayoutInflater.from(requireContext())
            .inflate(resourceId, root, false)
    }

    abstract fun getBottomSheetResourceId(): Int

    abstract fun getContentLayoutId(): Int

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        setDefaultValues()
    }

    private fun setDefaultValues() {
        bottomSheetBehavior.run {
            peekHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_PEEK_HEIGHT,
                resources.displayMetrics
            ).toInt()
            state = currentState
            isFitToContents = isFitToContent
            isHideable = defaultHide
            halfExpandedRatio = HALF_EXPANDED_RATIO
            setExpandedOffset(EXPANDED_OFFSET)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        isMapReady = true
    }

    override fun onGlobalLayout() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            mapView?.viewTreeObserver?.removeGlobalOnLayoutListener(this)
        } else {
            mapView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
        }
        isViewReady = true
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            if (locationResult.locations.isNotEmpty()) {
                currentLocation = locationResult.locations[0]
            }
        }
    }
}