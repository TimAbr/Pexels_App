package com.example.pexelsapp.data.datasources.user.remote

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.example.pexelsapp.domain.features.user.models.Uri
import com.example.pexelsapp.domain.features.user.models.Url
import com.example.pexelsapp.domain.features.user.repositories.UserError
import com.example.pexelsapp.utils.models.Outcome
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImgBBUserDataSource @Inject constructor(
    private val imgBBApi: ImgBBApi,
    @ImgBBApiKey private val apiKey: String,
    @ApplicationContext private val context: Context
) {
    private companion object {
        const val TAG = "ImgBBUserDataSource"
    }

    suspend fun uploadUserPhoto(userId: String, uri: Uri): Outcome<Url, UserError.Update> {
        Log.d(TAG, "Starting photo upload to ImgBB for user $userId. Uri: ${uri.value}")
        
        return try {
            val androidUri = uri.value.toUri()
            
            val inputStream = context.contentResolver.openInputStream(androidUri)
            val file = File(context.cacheDir, "upload_avatar_${userId}.jpg")
            file.outputStream().use { output ->
                inputStream?.copyTo(output)
            }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = imgBBApi.uploadImage(apiKey, body)

            if (response.success) {
                Log.d(TAG, "Photo uploaded to ImgBB successfully. URL: ${response.data.url}")
                Outcome.Success(Url(response.data.url))
            } else {
                Log.e(TAG, "ImgBB upload failed: ${response.status}")
                Outcome.Error(UserError.Common.Unknown)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload photo to ImgBB for user $userId", e)
            Outcome.Error(UserError.Common.Unknown)
        }
    }
}
