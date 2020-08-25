package com.example.samplemapdemo.data.network

import com.example.samplemapdemo.data.model.LocationResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    /**
     * Get the list of the locations from the API
     */
    @GET("/tht/locations.json")
    suspend fun getLocations(): Response<LocationResponse>
}