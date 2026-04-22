package com.example.pexelsapp.presentation.features.main_screen.settings.user.photo.models

import androidx.activity.ComponentActivity

interface LifecycleAware {
    fun bind(activity: ComponentActivity)
    fun unbind(activity: ComponentActivity)
}