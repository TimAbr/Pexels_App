package com.example.pexelsapp.presentation.features.main_screen.settings.user.photo.models

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.pexelsapp.domain.features.user.models.Uri
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

class CameraImageProvider : ImageProvider, LifecycleAware {

    private var launcher: ActivityResultLauncher<android.net.Uri>? = null
    private var continuation: CancellableContinuation<Uri?>? = null
    private var tempAndroidUri: android.net.Uri? = null
    
    private var _activity: ComponentActivity? = null
    private var activity: ComponentActivity
        get() = _activity ?: throw IllegalStateException("Activity not bound")
        set(value) {
            _activity = value
        }

    override fun bind(activity: ComponentActivity) {
        this.activity = activity
        launcher = activity.activityResultRegistry.register(
            REGISTRY_KEY,
            activity,
            ActivityResultContracts.TakePicture()
        ) { success ->
            val cont = continuation
            if (cont != null && cont.isActive) {
                if (success) {
                    cont.resume(tempAndroidUri?.let { Uri(it.toString()) })
                } else {
                    cont.resume(null)
                }
            }
            continuation = null
        }
    }

    override fun unbind(activity: ComponentActivity) {
        launcher?.unregister()
        launcher = null
        _activity = null
    }

    override suspend fun getImage(): Uri? = suspendCancellableCoroutine { cont ->
        continuation = cont
        cont.invokeOnCancellation {
            continuation = null
        }

        try {
            val file = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, activity.externalCacheDir)
            val authority = "${activity.packageName}$FILE_PROVIDER_SUFFIX"
            val androidUri = FileProvider.getUriForFile(activity, authority, file)
            tempAndroidUri = androidUri

            val currentLauncher = launcher
            if (currentLauncher != null && cont.isActive) {
                currentLauncher.launch(androidUri)
            } else if (cont.isActive) {
                cont.resume(null)
            }
        } catch (e: Exception) {
            if (cont.isActive) {
                cont.resume(null)
            }
        }
    }

    companion object {
        private const val REGISTRY_KEY = "camera_provider_key"
        private const val TEMP_FILE_PREFIX = "avatar_capture_"
        private const val TEMP_FILE_SUFFIX = ".jpg"
        private const val FILE_PROVIDER_SUFFIX = ".fileprovider"
    }
}
