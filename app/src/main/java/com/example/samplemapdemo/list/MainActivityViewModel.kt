package com.example.samplemapdemo.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samplemapdemo.data.model.LocationInfo
import com.example.samplemapdemo.data.model.Result
import com.example.samplemapdemo.data.repository.LocationRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(var locationRepo: LocationRepository) :
    ViewModel() {

    var isDialogVisible: Boolean = false
    var toAddLocation: LocationInfo? = null
    var description: String? = null

    var locations: LiveData<Result<List<LocationInfo>>> = locationRepo.locationInfoLiveData

    fun getLocations() {
        viewModelScope.launch {
            locationRepo.getLocation()
        }
    }

    fun addLocation(locationInfo: LocationInfo) {
        viewModelScope.launch {
            locationRepo.addLocation(locationInfo)
            locationRepo.getLocation()
        }
    }

    fun updateDescription(locationInfo: LocationInfo) {
        viewModelScope.launch {
            locationRepo.updateLocation(locationInfo)
        }
    }
}