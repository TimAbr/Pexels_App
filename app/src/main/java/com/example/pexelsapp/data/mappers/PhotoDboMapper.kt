package com.example.pexelsapp.data.mappers

import com.example.pexelsapp.data.models.PhotoDbo
import com.example.pexelsapp.data.models.PhotoSourceDbo
import com.example.pexelsapp.data.models.PhotographerDbo
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.models.PhotoSource
import com.example.pexelsapp.domain.common.models.Photographer
import javax.inject.Inject

class PhotoDboMapper @Inject constructor() {
    operator fun invoke(photo: Photo): PhotoDbo = with(photo){
        PhotoDbo(
            id = id,
            width = width,
            height = height,
            description = description,
            avgColor = avgColor,
            photographer = PhotographerDbo(
                id = photographer.id,
                name = photographer.name
            ),
            source = PhotoSourceDbo(
                original = source.original,
                large = source.large,
                medium = source.medium,
                small = source.small,
                tiny = source.tiny
            )
        )
    }

    operator fun invoke(photoDbo: PhotoDbo): Photo = with(photoDbo){
        Photo(
            id = id,
            width = width,
            height = height,
            description = description,
            avgColor = avgColor,
            photographer = Photographer(
                id = photographer.id,
                name = photographer.name
            ),
            source = PhotoSource(
                original = source.original,
                large = source.large,
                medium = source.medium,
                small = source.small,
                tiny = source.tiny
            )
        )
    }
}