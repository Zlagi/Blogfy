package com.zlagi.network.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zlagi.common.utils.Extensions
import com.zlagi.data.source.network.account.AccountNetworkDataSource
import com.zlagi.data.source.network.auth.AuthNetworkDataSource
import com.zlagi.data.source.network.blog.BlogNetworkDataSource
import com.zlagi.network.apiservice.AccountApiService
import com.zlagi.network.apiservice.AuthApiService
import com.zlagi.network.apiservice.BlogApiService
import com.zlagi.network.source.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class]
)
abstract class TestNetworkModule {

    @Binds
    abstract fun provideAuthNetworkDataSource(authNetworkDataSource: DefaultAuthNetworkDataSource): AuthNetworkDataSource

    @Binds
    abstract fun provideBlogNetworkDataSource(blogNetworkDataSourceTest: DefaultBlogNetworkDataSource): BlogNetworkDataSource

    @Binds
    abstract fun provideAccountNetworkDataSource(accountNetworkDataSourceTest: DefaultAccountNetworkDataSource): AccountNetworkDataSource

    companion object {

        @Provides
        @Singleton
        fun providesFirebaseAuth() = Firebase.auth

        @Provides
        @Singleton
        fun provideMockWebServer(): MockWebServer {
            return MockWebServer()
        }

        @Provides
        @Singleton
        fun provideAuthApi(builder: Retrofit.Builder): AuthApiService {
            return builder
                .build()
                .create(AuthApiService::class.java)
        }

        @Provides
        @Singleton
        fun provideAccountApi(builder: Retrofit.Builder): AccountApiService {
            return builder
                .build()
                .create(AccountApiService::class.java)
        }

        @Provides
        @Singleton
        fun provideBlogApi(builder: Retrofit.Builder): BlogApiService {
            return builder
                .build()
                .create(BlogApiService::class.java)
        }

        @Provides
        fun provideRetrofit(
            okHttpClient: OkHttpClient,
            mockWebServer: MockWebServer
        ): Retrofit.Builder {
            return Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(Extensions.moshi))
        }

        @Singleton
        @Provides
        fun provideOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .build()
        }
    }
}