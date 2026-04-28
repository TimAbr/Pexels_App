package com.example.pexelsapp.data.mappers

import com.example.pexelsapp.data.models.BookmarkedPhotoDto
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.features.bookmarks.models.BookmarkedPhoto
import javax.inject.Inject

class BookmarkedPhotoDtoMapper @Inject constructor() {
    fun toDomain(
        dto: BookmarkedPhotoDto,
        photo: Photo,
    ): BookmarkedPhoto {
        return BookmarkedPhoto(
            photo = photo,
            addedAt = dto.addedAt,
        )
    }

    fun toDto(
        domain: BookmarkedPhoto,
    ): BookmarkedPhotoDto {
        return BookmarkedPhotoDto(
            photoId = domain.photo.id,
            addedAt = domain.addedAt,
        )
    }
}
