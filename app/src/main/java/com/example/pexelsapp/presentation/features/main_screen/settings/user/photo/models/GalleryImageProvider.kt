package com.example.pexelsapp.presentation.features.main_screen.settings.user.photo.models

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pexelsapp.domain.features.user.models.Uri
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class GalleryImageProvider : ImageProvider, LifecycleAware {

    private var launcher: ActivityResultLauncher<PickVisualMediaRequest>? = null
    private var continuation: CancellableContinuation<Uri?>? = null

    override fun bind(activity: ComponentActivity) {
        launcher = activity.activityResultRegistry.register(
            KEY,
            activity,
            ActivityResultContracts.PickVisualMedia(),
        ) { androidUri ->

            val cont = continuation
            if (cont != null && cont.isActive) {
                cont.resume(androidUri?.let { Uri(it.toString()) })
            }
            continuation = null
        }
    }

    override fun unbind(activity: ComponentActivity) {
        launcher?.unregister()
        launcher = null
        continuation?.let {
            if (it.isActive) it.resume(null)
        }
        continuation = null
    }

    override suspend fun getImage(): Uri? = suspendCancellableCoroutine { cont ->
        continuation?.let {
            if (it.isActive) {
                it.resume(null)
            }
        }
        
        continuation = cont
        cont.invokeOnCancellation {
            if (continuation == cont) {
                continuation = null
            }
        }

        val currentLauncher = launcher
        if (currentLauncher != null && cont.isActive) {
            currentLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly,
                ),
            )
        } else if (cont.isActive) {
            cont.resume(null)
        }
    }

    companion object {
        private const val KEY = "gallery_provider_key"
    }
}
