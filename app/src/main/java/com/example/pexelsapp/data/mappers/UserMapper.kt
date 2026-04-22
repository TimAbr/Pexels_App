package com.example.pexelsapp.data.mappers

import com.example.pexelsapp.domain.features.user.models.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import javax.inject.Inject

class UserMapper @Inject constructor() {
    
    fun map(firebaseUser: FirebaseUser): User {
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            name = firebaseUser.displayName ?: "",
            photoUrl = firebaseUser.photoUrl?.toString() ?: ""
        )
    }

    fun map(document: DocumentSnapshot): User? {
        val id = document.id
        val email = document.getString("email") ?: return null
        val name = document.getString("name") ?: ""
        val photoUrl = document.getString("photoUrl") ?: ""
        
        return User(
            id = id,
            email = email,
            name = name,
            photoUrl = photoUrl
        )
    }

    fun toMap(user: User): Map<String, Any> {
        return mapOf(
            "email" to user.email,
            "name" to user.name,
            "photoUrl" to user.photoUrl
        )
    }
}
