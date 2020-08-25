package com.example.samplemapdemo.di

import com.example.samplemapdemo.main.SampleMapApp
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Suppress("DEPRECATION")
@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class, AndroidModule::class, MyViewModelModule::class, AppModule::class]
)
interface AppComponent: AndroidInjector<SampleMapApp> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<SampleMapApp>()
}