package com.example.pexelsapp.data.datasources.photos.remote

import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class AuthInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithAuth = originalRequest.newBuilder()
            .header("Authorization", apiKey)
            .build()
        return chain.proceed(requestWithAuth)
    }
}

fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()
}

fun providePexelsApi(okHttpClient: OkHttpClient, json: Json): PexelsApi{
    val contentType = "application/json".toMediaType()
    return Retrofit.Builder()
        .baseUrl("https://api.pexels.com/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
        .create(PexelsApi::class.java)

}