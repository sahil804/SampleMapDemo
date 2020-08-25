package com.example.samplemapdemo.data.network

import javax.inject.Inject

class LocationDataSource @Inject constructor(
    private val apiInterface: ApiInterface
) : BaseDataSource() {
    suspend fun getLocationsInfo() = getResult { apiInterface.getLocations() }
}