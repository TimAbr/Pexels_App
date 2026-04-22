package com.example.pexelsapp.data.datasources.user.remote

import com.example.pexelsapp.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ImgBBApiKey

@Module
@InstallIn(SingletonComponent::class)
object ImgBBModule {

    @Provides
    @Singleton
    fun provideImgBBApi(
        json: Json
    ): ImgBBApi {
        val contentType = "application/json".toMediaType()
        
        return Retrofit.Builder()
            .baseUrl(ImgBBApi.BASE_URL)
            .client(OkHttpClient()) 
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(ImgBBApi::class.java)
    }

    @Provides
    @ImgBBApiKey
    fun provideImgBBApiKey(): String = BuildConfig.IMGBB_API_KEY
}
