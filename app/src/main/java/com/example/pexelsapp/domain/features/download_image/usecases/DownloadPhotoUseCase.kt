package com.example.pexelsapp.domain.features.download_image.usecases

import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.features.download_image.ImageDownloader
import javax.inject.Inject

class DownloadPhotoUseCase @Inject constructor(
    private val imageDownloader: ImageDownloader
) {
    operator fun invoke(photo: Photo) {
        val url = photo.source.original
        val fileName = url.substringAfterLast('/')
        imageDownloader.downloadImage(url, fileName)
    }
}