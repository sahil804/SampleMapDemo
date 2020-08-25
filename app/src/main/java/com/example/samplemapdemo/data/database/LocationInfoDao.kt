package com.example.samplemapdemo.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.samplemapdemo.data.model.LocationInfo

@Dao
interface LocationInfoDao {

    @get:Query("SELECT * FROM LocationInfo")
    val all: List<LocationInfo>

    @Insert
    fun insertAll( locations: List<LocationInfo>)

    @Insert
    fun insert( location: LocationInfo)

    @Update
    fun update(locationInfo: LocationInfo): Int


    @get:Query("DELETE FROM LocationInfo")
    val deleteAll: Int

}