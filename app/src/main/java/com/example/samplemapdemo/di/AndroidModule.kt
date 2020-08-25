package com.example.samplemapdemo.di

import com.example.samplemapdemo.detail.LocationDetailFragment
import com.example.samplemapdemo.list.LocationListFragment
import com.example.samplemapdemo.main.AddMarkerDialogFragment
import com.example.samplemapdemo.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AndroidModule {

    @ContributesAndroidInjector
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun bindLocationListFragment(): LocationListFragment

    @ContributesAndroidInjector
    internal abstract fun bindLocationDetailFragment(): LocationDetailFragment

    @ContributesAndroidInjector
    internal abstract fun bindAddMarkerDialogFragment(): AddMarkerDialogFragment
}