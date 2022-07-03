package com.example.christmaswishlist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class GiftCamera : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null

    //Lateinit variables
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var viewFinder: PreviewView

    //Constant variables
    companion object {
        private const val TAG = "CameraXGift"

        //Unique file names are guaranteed
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSSS"
        private const val REQUEST_CODE_PERMISSION = 10
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_camera)

        val btnPhoto = findViewById<Button>(R.id.btnPhoto)
        viewFinder = findViewById(R.id.viewFinder)

        // Request camera permissions
        if (allPermissionsGranted()) { //If the camera permission is granted
            startCamera()
        } else {
            ActivityCompat.requestPermissions( //If the permission is not granted
                this, REQUIRED_PERMISSION,
                REQUEST_CODE_PERMISSION
            )

        }

        //Set up the listener for the take photo button
        btnPhoto.setOnClickListener {
            takePhoto()
        }


        //Set the output directory
        outputDirectory = getOutputDirectory()

        //Initialize the camera executor object
        cameraExecutor = Executors.newSingleThreadExecutor()


    }

    //Fetch the output directory
    private fun getOutputDirectory(): File {

        //externalMediaDirs requires a version greater than LOLLIPOP
        val mediaDir =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                externalMediaDirs.firstOrNull()?.let {
                    File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
                }
            } else {
                null
            }

        return if (mediaDir != null && mediaDir.exists()) {
            mediaDir
        } else {
            filesDir
        }
    }


    private fun startCamera() {
        //Binds the lifecycle of cameras to the lifecycle owner
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            //Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                }

            //select back camera as the default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            //Instantiate the imageCapture object
            imageCapture = ImageCapture.Builder().build()

            try {
                //Unbind use cases before rebinding
                cameraProvider.unbindAll()

                //Bind use cases to the camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e(TAG, "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))

    }

    private fun takePhoto() {

        //Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
                    + ".jpg"
        )


        //Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        //Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(e: ImageCaptureException) {
                    Log.e(TAG, "Photo Capture failed : ${e.message}", e)
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded : $savedUri"

                    //Show an alert dialog upon a successful image capture / save
                    val builder = AlertDialog.Builder(this@GiftCamera)

                    builder.setTitle("PHOTO SAVED")

                    builder.setMessage(msg)

                    //When the user clicks the "Okay" button of the alert dialog
                    builder.setNeutralButton("Okay") { _, _ ->
                        //Redirect the user to the next activity
                        val intent = Intent(this@GiftCamera, WishGift::class.java)

                        //Pass the file path of the saved image to the next activity
                        intent.putExtra("savedUri", savedUri.toString())

                        startActivity(intent)
                    }


                    val dialog: AlertDialog = builder.create()

                    dialog.show()

                }
            }
        )

    }


    //Checks if the required permissions are given
    private fun allPermissionsGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    //After the permission has been requested
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //If the request code is the camera permission request code
        if (requestCode == REQUEST_CODE_PERMISSION) {
            //Check the permission again
            if (allPermissionsGranted()) {
                //If granted, start the camera
                startCamera()
            } else {
                //The user denied the permission
                val builder = AlertDialog.Builder(this@GiftCamera)

                builder.setTitle("CAMERA PERMISSION DENIED")

                builder.setMessage("YOU HAVE DENIED THE PERMISSION REQUEST")

                builder.setNeutralButton("Okay") { _, _ ->
                    finish()
                }


                val dialog: AlertDialog = builder.create()

                dialog.show()
            }
        }
    }

}