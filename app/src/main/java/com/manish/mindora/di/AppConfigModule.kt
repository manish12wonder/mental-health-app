package com.manish.mindora.di

import com.manish.mindora.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    @Provides
    @Singleton
    @Named("sentiment_api_key")
    fun provideSentimentApiKey(): String = BuildConfig.SENTIMENT_API_KEY
}
