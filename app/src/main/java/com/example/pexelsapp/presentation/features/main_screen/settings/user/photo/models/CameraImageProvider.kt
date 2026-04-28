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
import com.example.pexelsapp.R
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
    private var tempFile: File? = null
    
    private var _activity: ComponentActivity? = null
    private val activity: ComponentActivity
        get() = _activity ?: throw IllegalStateException("Activity not bound")

    override fun bind(activity: ComponentActivity) {
        this._activity = activity
        
        val registry = activity.activityResultRegistry

        cameraLauncher = registry.register(
            CAMERA_CAPTURE_KEY,
            activity,
            ActivityResultContracts.TakePicture(),
        ) { success ->
            val cont = continuation
            if (cont != null && cont.isActive) {
                if (success) {
                    cont.resume(tempAndroidUri?.let { Uri(it.toString()) })
                } else {
                    deleteTempFile()
                    cont.resume(null)
                }
            } else {
                deleteTempFile()
            }
            continuation = null
        }

        permissionLauncher = registry.register(
            CAMERA_PERMISSION_KEY,
            activity,
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            if (granted) {
                launchCameraInternal()
            } else {
                Toast.makeText(
                    activity,
                    R.string.error_camera_permission,
                    Toast.LENGTH_SHORT,
                ).show()
                val cont = continuation
                if (cont != null && cont.isActive) {
                    cont.resume(null)
                }
                continuation = null
            }
        }
    }

    override fun unbind(activity: ComponentActivity) {
        cameraLauncher?.unregister()
        permissionLauncher?.unregister()
        cameraLauncher = null
        permissionLauncher = null
        _activity = null
        
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
                deleteTempFile()
            }
        }

        val hasPermission = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            launchCameraInternal()
        } else {
            val pLauncher = permissionLauncher
            if (pLauncher != null) {
                pLauncher.launch(Manifest.permission.CAMERA)
            } else {
                cont.resume(null)
                continuation = null
            }
        }
    }

    private fun launchCameraInternal() {
        try {
            val storageDir = activity.externalCacheDir ?: activity.cacheDir
            deleteTempFile()
            
            val file = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, storageDir)
            tempFile = file
            
            val authority = "${activity.packageName}.fileprovider"
            val androidUri = FileProvider.getUriForFile(activity, authority, file)
            tempAndroidUri = androidUri

            val cLauncher = cameraLauncher
            if (cLauncher != null) {
                cLauncher.launch(androidUri)
            } else {
                handleError("Camera launcher is null")
            }
        } catch (e: Exception) {
            handleError(e.message ?: "Unknown error")
        }
    }

    private fun deleteTempFile() {
        tempFile?.let {
            if (it.exists()) {
                it.delete()
            }
        }
        tempFile = null
        tempAndroidUri = null
    }

    private fun handleError(message: String) {
        Log.e(TAG, "Error: $message")
        Toast.makeText(
            activity,
            R.string.error_camera_launch,
            Toast.LENGTH_SHORT,
        ).show()
        val cont = continuation
        if (cont != null && cont.isActive) {
            cont.resume(null)
        }
        continuation = null
        deleteTempFile()
    }

    companion object {
        private const val TAG = "CameraImageProvider"
        private const val TEMP_FILE_PREFIX = "avatar_capture_"
        private const val TEMP_FILE_SUFFIX = ".jpg"
        private const val CAMERA_CAPTURE_KEY = "camera_capture_key"
        private const val CAMERA_PERMISSION_KEY = "camera_permission_key"
    }
}
