package com.adira.signmaster.ui.translate.camera_translate

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.URI
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.adira.signmaster.databinding.ActivityCameraTranslateBinding

class CameraTranslateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraTranslateBinding
    private lateinit var cameraExecutor: ExecutorService
    private var webSocketClient: WebSocketClient? = null
    private val isConnecting = MutableLiveData<Boolean>()
    private var isProcessingFrame = false

    private var lastFrameSentTime = 0L
    private val FRAME_INTERVAL = 100L
    private var totalFramesReceived = 0
    private var totalFramesSent = 0
    private var isUsingFrontCamera = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraTranslateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        isConnecting.observe(this) { connecting ->
            binding.progressBar.visibility = if (connecting) View.VISIBLE else View.GONE
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.fabSwitchCamera.setOnClickListener {
            isUsingFrontCamera = !isUsingFrontCamera
            startCamera()
        }

        setupWebSocket()
        initializeCamera()
    }


    private fun initializeCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun setupWebSocket() {
        isConnecting.postValue(true)
        val serverUrl = "wss://inference-model-kji5w4ybbq-et.a.run.app/"
        webSocketClient = object : WebSocketClient(URI(serverUrl)) {
            override fun onOpen(handshake: ServerHandshake?) {
                Log.d(TAG, "WebSocket connection established")
                runOnUiThread {
                    isConnecting.postValue(false)
                    Toast.makeText(applicationContext, "Server Connected", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onMessage(message: String?) {
                message?.let { handleServerResponse(it) }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "WebSocket connection closed: $reason")
                runOnUiThread {
                    isConnecting.postValue(false)
                    Toast.makeText(applicationContext, "Server Disconnected", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onError(ex: Exception?) {
                Log.e(TAG, "WebSocket error occurred: ${ex?.message}")
                runOnUiThread {
                    isConnecting.postValue(false)
                }
            }
        }.apply { connect() }
    }

    private fun updatePredictionDisplay(predictions: JSONArray, landmarks: JSONArray?) {
        runOnUiThread {
            val predictionText = buildString {
                for (i in 0 until predictions.length()) {
                    val prediction = predictions.getJSONObject(i)
                    val sign = prediction.getString("sign")
                    val confidence = prediction.getDouble("confidence")
                    append("#${i + 1}: $sign (${String.format("%.2f", confidence * 100)}%)\n")
                }
                landmarks?.let {
                    append("\nDetected ${it.length()} landmarks")
                }
            }
            binding.predictionText.text = predictionText
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector = if (isUsingFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            val preview = Preview.Builder()
                .build()
                .also { it.surfaceProvider = binding.viewFinder.surfaceProvider }

            // Configure image analysis
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImage(imageProxy)
                    }
                }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to bind camera use cases: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImage(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()
        totalFramesReceived++

        if (isProcessingFrame || currentTime - lastFrameSentTime < FRAME_INTERVAL) {
            imageProxy.close()
            return
        }

        try {
            isProcessingFrame = true
            lastFrameSentTime = currentTime

            val bitmap = imageProxy.toBitmap()
            val rotatedBitmap = rotateBitmap(bitmap, imageProxy.imageInfo.rotationDegrees.toFloat())

            val outputStream = ByteArrayOutputStream()
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)

            val base64Image = Base64.encodeToString(
                outputStream.toByteArray(),
                Base64.NO_WRAP
            )

            webSocketClient?.takeIf { it.isOpen }?.let { client ->
                client.send(base64Image)
                totalFramesSent++
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error processing image: ${e.message}")
        } finally {
            isProcessingFrame = false
            imageProxy.close()
        }
    }


    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(
            bitmap,
            0, 0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        webSocketClient?.close()
    }

    private val paint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val circlePaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
        strokeWidth = 3f
    }

    private val handConnections = listOf(
        // Thumb
        Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 4),
        // Index finger
        Pair(0, 5), Pair(5, 6), Pair(6, 7), Pair(7, 8),
        // Middle finger
        Pair(0, 9), Pair(9, 10), Pair(10, 11), Pair(11, 12),
        // Ring finger
        Pair(0, 13), Pair(13, 14), Pair(14, 15), Pair(15, 16),
        // Pinky
        Pair(0, 17), Pair(17, 18), Pair(18, 19), Pair(19, 20),
        // Palm
        Pair(5, 9), Pair(9, 13), Pair(13, 17)
    )

    private fun handleServerResponse(message: String) {
        try {
            val response = JSONObject(message)
            val handDetected = response.getBoolean("hand_detected")

            if (handDetected) {
                val predictions = response.getJSONArray("predictions")
                val landmarks = response.optJSONArray("landmarks")

                val drawingBitmap = Bitmap.createBitmap(
                    binding.viewFinder.width,
                    binding.viewFinder.height,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(drawingBitmap)
                landmarks?.let {
                    drawHandLandmarks(canvas, it)
                }

                runOnUiThread {
                    updatePredictionDisplay(predictions,landmarks)
                    binding.overlayView.setImageBitmap(drawingBitmap)
                }
            } else {
                runOnUiThread {
                    binding.predictionText.text = "No hand detected"
                    binding.overlayView.setImageBitmap(null)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing response", e)
        }
    }

    private fun drawHandLandmarks(canvas: Canvas, landmarks: JSONArray) {
        val width = canvas.width.toFloat()
        val height = canvas.height.toFloat()
        val points = mutableListOf<PointF>()

        for (i in 0 until landmarks.length()) {
            val landmark = landmarks.getJSONObject(i)
            val x = landmark.getDouble("x").toFloat() * width
            val y = landmark.getDouble("y").toFloat() * height
            points.add(PointF(x, y))

            canvas.drawCircle(x, y, 8f, circlePaint)
        }

        for ((start, end) in handConnections) {
            if (points.size > start && points.size > end) {
                val startPoint = points[start]
                val endPoint = points[end]
                canvas.drawLine(
                    startPoint.x, startPoint.y,
                    endPoint.x, endPoint.y,
                    paint
                )
            }
        }
    }

    companion object {
        private const val TAG = "CameraTranslateActivity"
    }
}
