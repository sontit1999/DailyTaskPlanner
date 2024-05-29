package com.ls.dailytaskplanner.di

import android.content.Context
import com.ls.dailytaskplanner.cmp.GoogleMobileAdsConsentManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {
    @Singleton
    @Provides
    fun providerCMP(@ApplicationContext context: Context) = GoogleMobileAdsConsentManager(context)
}