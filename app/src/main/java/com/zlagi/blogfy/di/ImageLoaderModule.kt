package com.zlagi.blogfy.di

import android.content.Context
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageLoaderModule {

    companion object {
        @Provides
        @Singleton
        fun provideImageLoader(
            @ApplicationContext context: Context
        ): ImageLoader {
            return ImageLoader.Builder(context).build()
        }
    }
}