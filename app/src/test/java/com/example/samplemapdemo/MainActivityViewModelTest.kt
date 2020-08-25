package com.example.samplemapdemo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.samplemapdemo.data.model.LocationInfo
import com.example.samplemapdemo.data.model.Result
import com.example.samplemapdemo.data.repository.LocationRepository
import com.example.samplemapdemo.list.MainActivityViewModel
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainActivityViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var locationRepository: LocationRepository

    @Test
    fun testRepositoryFunctions() {
        runBlocking {
            MockitoAnnotations.initMocks(this)
            val viewModel = MainActivityViewModel(locationRepository)
            viewModel.getLocations()
            verify(locationRepository).getLocation()
            val locationInfo: LocationInfo = mock()
            viewModel.addLocation(locationInfo)
            verify(locationRepository).addLocation(locationInfo)
            viewModel.updateDescription(locationInfo)
            verify(locationRepository).updateLocation(locationInfo)
        }
    }
}