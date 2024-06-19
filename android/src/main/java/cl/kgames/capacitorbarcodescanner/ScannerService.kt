package cl.kgames.capacitorbarcodescanner

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

interface BarcodeResultListener {
    fun onBarcodeResult(code: String?)
}

class ScannerService(private val context: Context, private val lifecycleOwner: LifecycleOwner,
                     private val barcodeResultListener: BarcodeResultListener) {
    private lateinit var previewView: PreviewView
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
        private const val TAG = "ScannerService"
        private const val PERMISSION_CAMERA_REQUEST = 1
    }

    init {
        setupCameraProvider()
    }

    private fun setupCameraProvider() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                if (::previewView.isInitialized && isCameraPermissionGranted()) {
                    bindCameraUseCases()
                }
            } catch (e: ExecutionException) {
                Log.e(TAG, "Unhandled exception", e)
            } catch (e: InterruptedException) {
                Log.e(TAG, "Unhandled exception", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun prepareCamera(previewView: PreviewView?) {
        this.previewView = previewView!!
        this.previewView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white)) // Set white background initially
        if (isCameraPermissionGranted()) {
            bindCameraUseCases()
        } else {
            ActivityCompat.requestPermissions(
                (context as Activity), arrayOf(Manifest.permission.CAMERA),
                PERMISSION_CAMERA_REQUEST)
        }
    }

    fun startCamera(previewView: PreviewView?) {
        this.previewView = previewView!!
        if (cameraProvider != null) {
            bindCameraUseCases()
        } else {
            setupCameraProvider()
        }
    }

    fun stopCamera() {
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            cameraProvider?.unbindAll()
        }
        //executor.shutdown()
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) return
        previewUseCase?.let { cameraProvider!!.unbind(it) }

        previewUseCase = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(screenRotation)
            .build().also { preview ->
                preview.setSurfaceProvider { request ->
                    val surfaceProvider = previewView!!.createSurfaceProvider()
                    surfaceProvider.onSurfaceRequested(request)
                    previewView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent)) // Remove background when preview starts
                }
            }

        try {
            cameraProvider?.bindToLifecycle(lifecycleOwner, cameraSelector, previewUseCase)
        } catch (ex: Exception) {
            Log.e(TAG, "Error binding preview use case: ${ex.localizedMessage}")
        }
    }

    private fun bindCameraUseCases() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }

    private fun bindAnalyseUseCase() {

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_DATA_MATRIX)
            .build()

        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(options)


        if (cameraProvider == null) {
            return
        }
        /*
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }*/
        analysisUseCase?.let { cameraProvider!!.unbind(it) }

        analysisUseCase = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(screenRotation)
            .build()

        analysisUseCase?.setAnalyzer(
            executor,
            ImageAnalysis.Analyzer { imageProxy ->
                processImageProxy(barcodeScanner, imageProxy)
            }
        )

        try {
            cameraProvider!!.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                analysisUseCase!!
            )
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun processImageProxy(barcodeScanner: BarcodeScanner, imageProxy: ImageProxy) {
        val inputImage = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                /*if (barcodes.isNotEmpty()) {
                    val code = barcodes[0].rawValue
                    handleBarcodeDetected(code)
                }*/
                /*if(barcodes.size > 0){
                    var code = barcodes[0]
                    handleBarcodeDetected(code.rawValue!!)
                }*/
                if (barcodes.isNotEmpty()) {
                    val code = barcodes[0].rawValue
                    Log.d(TAG, "Barcode detected: $code")
                    handleBarcodeDetected(code)
                } else {
                    //Log.d(TAG, "No barcode detected")
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Error processing image", it)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun handleBarcodeDetected(code: String?) {
        Log.d(TAG, "Barcode detected: $code")
        //cameraProvider?.unbindAll() // 停止相机预览
        stopCamera() // 停止相机预览
        // 这里可以添加代码来处理扫描结果，例如通过回调接口发送事件
        barcodeResultListener.onBarcodeResult(code) // 通知扫描结果
    }

    // 动态计算屏幕的最佳纵横比
    private val screenAspectRatio: Int
        get() {
            val metrics = DisplayMetrics()
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(metrics)
            return aspectRatio(metrics.widthPixels, metrics.heightPixels)
        }

    // 获取屏幕的旋转角度
    private val screenRotation: Int
        get() {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            return windowManager.defaultDisplay.rotation
        }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = Math.max(width, height).toDouble() / Math.min(width, height)
        if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bindCameraUseCases()
            } else {
                Log.e(TAG, "Permission not granted by the user.")
            }
        }
    }

}
