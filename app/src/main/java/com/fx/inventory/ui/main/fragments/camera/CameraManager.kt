package com.fx.inventory.ui.main.fragments.camera

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.fx.inventory.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class CameraManager(
    private val viewFinder: PreviewView,
    val context: Context,
    val onPictureCaptured: (Uri) -> Unit,
    val onErrorCapturingImage: (String) -> Unit,
    private val viewLifecycleOwner: LifecycleOwner,

) :ImageCapture.OnImageSavedCallback {
    val cameraExecutor = Executors.newSingleThreadExecutor()
    private var imageCapture: ImageCapture? = null
    private var photoFile:File? =  null
    lateinit var cameraProvider: ProcessCameraProvider

    companion object {
        private const val TAG = "CameraManager"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }


    private fun getOutputDirectory(): File {
        val mediaDir = context.externalMediaDirs?.firstOrNull()?.let {
            File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else context.filesDir!!
    }


    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
             cameraProvider= cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(context))
    }

    fun takePicture() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        photoFile = File(
            getOutputDirectory(),
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile!!).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(outputOptions, cameraExecutor, this)

    }

    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
        if(photoFile!=null) {
            val savedUri = Uri.fromFile(photoFile)

            CoroutineScope(Dispatchers.Main).launch {
                cameraProvider.unbindAll()
            }
            onPictureCaptured(savedUri)
        }
    }

    override fun onError(exception: ImageCaptureException) {
        Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
        onErrorCapturingImage(exception.message!!)
    }


    fun onFragmentDestroy(){
        cameraExecutor.shutdown()
    }


    fun restartCamera(){
        startCamera()
    }
}