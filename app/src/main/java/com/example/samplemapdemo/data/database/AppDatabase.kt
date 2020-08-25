package com.example.samplemapdemo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.samplemapdemo.data.model.LocationInfo

@Database(
    entities = [LocationInfo::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun locationInfoDao(): LocationInfoDao
}