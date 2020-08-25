package com.example.samplemapdemo.di

import androidx.lifecycle.ViewModel
import com.example.samplemapdemo.list.MainActivityViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MyViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMyViewModel(myViewModel: MainActivityViewModel): ViewModel
}