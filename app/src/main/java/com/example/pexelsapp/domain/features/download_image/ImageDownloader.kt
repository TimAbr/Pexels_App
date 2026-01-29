package com.example.pexelsapp.domain.features.download_image

interface ImageDownloader {
    fun downloadImage(url: String, fileName: String)
}