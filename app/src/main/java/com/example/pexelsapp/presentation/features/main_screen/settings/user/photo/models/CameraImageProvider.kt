package com.example.pexelsapp.presentation.features.main_screen.settings.user.photo.models

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.pexelsapp.domain.features.user.models.Uri
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

class CameraImageProvider : ImageProvider, LifecycleAware {

    private var cameraLauncher: ActivityResultLauncher<android.net.Uri>? = null
    private var permissionLauncher: ActivityResultLauncher<String>? = null
    
    private var continuation: CancellableContinuation<Uri?>? = null
    private var tempAndroidUri: android.net.Uri? = null
    
    private var _activity: ComponentActivity? = null
    private val activity: ComponentActivity
        get() = _activity ?: throw IllegalStateException("Activity not bound")

    override fun bind(activity: ComponentActivity) {
        Log.d(TAG, "Binding to ${activity.javaClass.simpleName}")
        this._activity = activity
        
        val registry = activity.activityResultRegistry

        cameraLauncher = registry.register(
            "camera_capture_${System.currentTimeMillis()}", // Unique key
            activity,
            ActivityResultContracts.TakePicture()
        ) { success ->
            Log.d(TAG, "Camera capture result: $success")
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

        permissionLauncher = registry.register(
            "camera_permission_${System.currentTimeMillis()}", // Unique key
            activity,
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            Log.d(TAG, "Permission result: $granted")
            if (granted) {
                launchCameraInternal()
            } else {
                Toast.makeText(activity, "Camera permission denied", Toast.LENGTH_SHORT).show()
                val cont = continuation
                if (cont != null && cont.isActive) {
                    cont.resume(null)
                }
                continuation = null
            }
        }
    }

    override fun unbind(activity: ComponentActivity) {
        Log.d(TAG, "Unbinding")
        cameraLauncher?.unregister()
        permissionLauncher?.unregister()
        cameraLauncher = null
        permissionLauncher = null
        _activity = null
        continuation = null
    }

    override suspend fun getImage(): Uri? = suspendCancellableCoroutine { cont ->
        Log.d(TAG, "getImage requested")
        continuation = cont
        cont.invokeOnCancellation {
            continuation = null
        }

        val hasPermission = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            launchCameraInternal()
        } else {
            Log.d(TAG, "Requesting permission...")
            val pLauncher = permissionLauncher
            if (pLauncher != null) {
                pLauncher.launch(Manifest.permission.CAMERA)
            } else {
                Log.e(TAG, "Permission launcher is null!")
                cont.resume(null)
                continuation = null
            }
        }
    }

    private fun launchCameraInternal() {
        try {
            val storageDir = activity.externalCacheDir ?: activity.cacheDir
            val file = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, storageDir)
            val authority = "${activity.packageName}.fileprovider"
            val androidUri = FileProvider.getUriForFile(activity, authority, file)
            tempAndroidUri = androidUri

            Log.d(TAG, "Launching camera launcher with URI: $androidUri")
            val cLauncher = cameraLauncher
            if (cLauncher != null) {
                cLauncher.launch(androidUri)
            } else {
                Log.e(TAG, "Camera launcher is null!")
                val cont = continuation
                if (cont != null && cont.isActive) {
                    cont.resume(null)
                }
                continuation = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch camera", e)
            Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            val cont = continuation
            if (cont != null && cont.isActive) {
                cont.resume(null)
            }
            continuation = null
        }
    }

    companion object {
        private const val TAG = "CameraImageProvider"
        private const val TEMP_FILE_PREFIX = "avatar_capture_"
        private const val TEMP_FILE_SUFFIX = ".jpg"
    }
}
