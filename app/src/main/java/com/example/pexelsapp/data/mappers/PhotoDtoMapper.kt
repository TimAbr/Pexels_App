package com.example.pexelsapp.data.mappers

import com.example.pexelsapp.data.models.PhotoDto
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.models.PhotoSource
import com.example.pexelsapp.domain.common.models.Photographer
import javax.inject.Inject

class PhotoDtoMapper @Inject constructor(){
    operator fun invoke(photoDto: PhotoDto): Photo = with(photoDto){
        Photo(
            id = id,
            width = width,
            height = height,
            photographer = Photographer(
                id = photographerId,
                name = photographer
            ),
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