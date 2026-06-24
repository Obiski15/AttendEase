package com.example.attendease.utils

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            Log.d("QrCodeAnalyzer", "analyze() called: width=${imageProxy.width}, height=${imageProxy.height}, rotation=$rotationDegrees")
            val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        Log.d("QrCodeAnalyzer", "Success: detected ${barcodes.size} barcodes")
                    }
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { value ->
                            Log.d("QrCodeAnalyzer", "Detected QR code value: $value")
                            onQrCodeScanned(value)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("QrCodeAnalyzer", "Failed to process image with ML Kit", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            Log.w("QrCodeAnalyzer", "analyze() received null mediaImage")
            imageProxy.close()
        }
    }
}
