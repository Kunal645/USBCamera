package com.example.usb_camera

import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest

class CameraZoomController(private val cameraManager: CameraManager, private val cameraId: String) {

    private var currentZoomLevel = 1.0f
    private var maxZoomLevel = 1.0f
    private var cameraCaptureRequest: CaptureRequest.Builder? = null

    init {
        // Initialize max zoom level
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        maxZoomLevel = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM) ?: 1.0f
    }

    fun setZoomLevel(zoomLevel: Float) {
        currentZoomLevel = zoomLevel.coerceIn(1.0f, maxZoomLevel)
        cameraCaptureRequest?.apply {
            set(CaptureRequest.SCALER_CROP_REGION, calculateZoomRect())
        }
    }

    private fun calculateZoomRect(): android.graphics.Rect {
        val sensorSize = cameraManager.getCameraCharacteristics(cameraId).get(
            CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE
        ) ?: return android.graphics.Rect()

        val centerX = sensorSize.width() / 2
        val centerY = sensorSize.height() / 2
        val deltaX = (0.5f * sensorSize.width() / currentZoomLevel).toInt()
        val deltaY = (0.5f * sensorSize.height() / currentZoomLevel).toInt()

        return android.graphics.Rect(
            centerX - deltaX,
            centerY - deltaY,
            centerX + deltaX,
            centerY + deltaY
        )
    }

    // Additional method to apply zoom level when creating the capture request
    fun applyZoom(captureRequest: CaptureRequest.Builder) {
        cameraCaptureRequest = captureRequest
        captureRequest.set(CaptureRequest.SCALER_CROP_REGION, calculateZoomRect())
    }
}
