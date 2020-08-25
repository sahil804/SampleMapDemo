package com.example.samplemapdemo.di

import androidx.room.Room
import com.example.samplemapdemo.main.SampleMapApp
import com.example.samplemapdemo.data.database.AppDatabase
import com.example.samplemapdemo.data.network.ApiInterface
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    internal fun buildDataBase(application: SampleMapApp): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java, "sample.db"
        ).build()
    }

    private val API_BASE_URL = "https://apps.cochlear.limited"
    @Provides
    @Singleton
    internal fun providePostApi(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }

    @Provides
    @Singleton
    internal fun provideRetrofitInterface(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
