package com.example.pexelsapp.data.services.image_downloader

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.example.pexelsapp.domain.features.download_image.ImageDownloader
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import javax.inject.Inject

@BoundTo(supertype = ImageDownloader::class, component = SingletonComponent::class)
class AndroidImageDownloader @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageDownloader {
    private val downloadManager =
        context.getSystemService(DownloadManager::class.java)

    override fun downloadImage(url: String, fileName: String) {
        val request = DownloadManager.Request(url.toUri())
            .setMimeType("image/jpeg")
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setTitle(fileName)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PICTURES, fileName
            )

        downloadManager.enqueue(request)
    }
}