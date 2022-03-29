package com.zlagi.network.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.zlagi.common.utils.Extensions.moshi
import com.zlagi.data.source.preferences.PreferencesDataSource
import com.zlagi.data.source.network.account.AccountNetworkDataSource
import com.zlagi.data.source.network.auth.AuthNetworkDataSource
import com.zlagi.data.source.network.blog.BlogNetworkDataSource
import com.zlagi.network.apiservice.AccountApiService
import com.zlagi.network.apiservice.AuthApiService
import com.zlagi.network.apiservice.BlogApiService
import com.zlagi.network.interceptor.AuthenticationInterceptor
import com.zlagi.network.mapper.TokensNetworkDataMapper
import com.zlagi.network.model.NetworkConstants
import com.zlagi.network.source.DefaultAccountNetworkDataSource
import com.zlagi.network.source.DefaultAuthNetworkDataSource
import com.zlagi.network.source.DefaultBlogNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    abstract fun provideAuthNetworkDataSource(authNetworkDataSource: DefaultAuthNetworkDataSource): AuthNetworkDataSource

    @Binds
    abstract fun provideBlogNetworkDataSource(blogNetworkDataSource: DefaultBlogNetworkDataSource): BlogNetworkDataSource

    @Binds
    abstract fun provideAccountNetworkDataSource(accountNetworkDataSource: DefaultAccountNetworkDataSource): AccountNetworkDataSource

    companion object {

        @Provides
        @Singleton
        fun providesFirebaseAuth() = Firebase.auth

        @Provides
        @Singleton
        fun providesFirebaseStorage() = FirebaseStorage.getInstance()

        @Provides
        @Singleton
        fun provideAuthApi(builder: Retrofit.Builder): AuthApiService {
            return builder
                .baseUrl(NetworkConstants.BASE_ENDPOINT)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
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
                .baseUrl(NetworkConstants.BASE_ENDPOINT)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(BlogApiService::class.java)
        }

        @Provides
        fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit.Builder {
            return Retrofit.Builder()
                .baseUrl(NetworkConstants.BASE_ENDPOINT)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(okHttpClient)
        }

        @Provides
        fun provideOkHttpClient(
            preferencesDataSource: PreferencesDataSource,
            httpLoggingInterceptor: HttpLoggingInterceptor,
            tokensNetworkDataMapper: TokensNetworkDataMapper
        ): OkHttpClient {
            val authenticationInterceptor = AuthenticationInterceptor(
                preferencesDataSource,
                tokensNetworkDataMapper,
                provideAuthApi(Retrofit.Builder()),
                Dispatchers.IO
            )
            return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(authenticationInterceptor)
                .build()
        }

        @Provides
        fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            return interceptor
        }
    }
}