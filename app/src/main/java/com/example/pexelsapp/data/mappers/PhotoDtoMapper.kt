package com.example.pexelsapp.data.mappers

import com.example.pexelsapp.data.models.PhotoDto
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.models.PhotoSource

class PhotoDtoMapper {
    operator fun invoke(photoDto: PhotoDto): Photo = with(photoDto){
        Photo(
            id = id,
            width = width,
            height = height,
            photographer = photographer,
            description = description,
            avgColor = avgColor,
            source = PhotoSource(
                original = src.original,
                large = src.large,
                medium = src.medium,
                small = src.small,
                tiny = src.tiny
            )
        )
    }
}