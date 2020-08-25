package com.example.samplemapdemo.main

import com.example.samplemapdemo.di.AppComponent
import com.example.samplemapdemo.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class SampleMapApp : DaggerApplication() {
    lateinit var component: AppComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        component = DaggerAppComponent.builder().create(this) as AppComponent
        return component
    }
}