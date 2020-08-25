package com.example.samplemapdemo.data.repository

import androidx.lifecycle.MutableLiveData
import com.example.samplemapdemo.data.database.AppDatabase
import com.example.samplemapdemo.data.model.LocationInfo
import com.example.samplemapdemo.data.model.Result
import com.example.samplemapdemo.data.model.Status
import com.example.samplemapdemo.data.network.LocationDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    val locationDataSource: LocationDataSource,
    val database: AppDatabase
) {

    var locationInfoLiveData = MutableLiveData<Result<List<LocationInfo>>>()

    suspend fun getLocation() {
        return withContext(Dispatchers.IO) {
            delay(200)
            locationInfoLiveData.postValue(Result.loading(null))
            val response = locationDataSource.getLocationsInfo()
            if (response.status == Status.SUCCESS) {
                val locationInfoList = mutableListOf<LocationInfo>()
                locationInfoList.addAll(response.data!!.locations)
                locationInfoList.addAll(database.locationInfoDao().all)
                locationInfoLiveData.postValue(Result.success(locationInfoList.toList()))
            } else {
                if (database.locationInfoDao().all.isNotEmpty()) {
                    locationInfoLiveData.postValue(Result.success(database.locationInfoDao().all))
                } else {
                    locationInfoLiveData.postValue(Result.error("Something went wrong", null))
                }
            }
        }
    }

    suspend fun addLocation(locationInfo: LocationInfo) {
        withContext(Dispatchers.IO) {
            database.locationInfoDao().insert(locationInfo)
            val updatedList = mutableListOf<LocationInfo>()
            locationInfoLiveData.value?.data?.takeIf { it.isNotEmpty() }?.let {
                updatedList.addAll(it)
            }
            updatedList.add(locationInfo)
            locationInfoLiveData.postValue(Result.success(updatedList.toList()))
        }
    }

    suspend fun updateLocation(locationInfo: LocationInfo) {
        withContext(Dispatchers.IO) {
            database.locationInfoDao().update(locationInfo)
        }
    }
}