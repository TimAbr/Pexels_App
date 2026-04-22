package com.example.pexelsapp.presentation.features.main_screen.settings.user.photo.models

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.pexelsapp.domain.features.user.models.Uri
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class ImagePickerManager @Inject constructor(
    private val providers: Map<ImageProviderType, @JvmSuppressWildcards ImageProvider>
) : DefaultLifecycleObserver {

    private var attachedActivity: ComponentActivity? = null

    fun bind(activity: ComponentActivity) {
        attachedActivity = activity
        activity.lifecycle.addObserver(this)

        providers.values.forEach { provider ->
            if (provider is LifecycleAware) {
                provider.bind(activity)
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        attachedActivity?.let { activity ->
            providers.values.forEach { provider ->
                if (provider is LifecycleAware) {
                    provider.unbind(activity)
                }
            }
        }
        attachedActivity = null
        super.onDestroy(owner)
    }

    suspend fun getImage(type: ImageProviderType): Uri? {
        return providers[type]?.getImage()
    }
}
