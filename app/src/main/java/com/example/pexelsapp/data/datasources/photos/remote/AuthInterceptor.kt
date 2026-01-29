package com.example.pexelsapp.data.datasources.photos.remote

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithAuth = originalRequest.newBuilder()
            .header("Authorization", apiKey)
            .build()
        return chain.proceed(requestWithAuth)
    }
}