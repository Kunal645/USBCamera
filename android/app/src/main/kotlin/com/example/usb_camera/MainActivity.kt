package com.example.usb_camera

import io.flutter.embedding.android.FlutterActivity
import android.app.Activity
import android.os.Build
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.embedding.engine.FlutterEngine
import java.util.logging.Logger
import android.util.Log

class MainActivity: FlutterActivity() {
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        flutterEngine.plugins.add(FlutterUVCCameraPlugin())
    }
}

class FlutterUVCCameraPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private val channelName = "flutter_uvc_camera/channel"
    private val viewName = "uvc_camera_view"
    private var channel: MethodChannel? = null
    private lateinit var mUVCCameraViewFactory: UVCCameraViewFactory
    private var activity: Activity? = null
    private var permissionResultListener: PermissionResultListener? = null
    private var mActivityPluginBinding: ActivityPluginBinding? = null
    private var requestPermissionsResultListener: io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener? =
        null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, channelName)
        channel!!.setMethodCallHandler(this)
        mUVCCameraViewFactory = UVCCameraViewFactory(this, channel!!)
        flutterPluginBinding.platformViewRegistry.registerViewFactory(viewName, mUVCCameraViewFactory)
    }


    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
        channel = null
    }


    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        mActivityPluginBinding = binding
        requestPermissionsResultListener =
            io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener { requestCode, permissions, grantResults ->
                permissionResultListener?.onPermissionResult(requestCode, permissions, grantResults)
                true
            }
        binding.addRequestPermissionsResultListener(requestPermissionsResultListener!!)
    }

    fun setPermissionResultListener(listener: PermissionResultListener) {
        this.permissionResultListener = listener
    }

    override fun onDetachedFromActivityForConfigChanges() {

    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {

    }

    override fun onDetachedFromActivity() {
        activity = null
        if (requestPermissionsResultListener != null) {
            mActivityPluginBinding?.removeRequestPermissionsResultListener(requestPermissionsResultListener!!)
            requestPermissionsResultListener = null
            mActivityPluginBinding = null
        }
    }


    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "initializeCamera" -> {
                mUVCCameraViewFactory.initCamera()
//                Camera.isZoomSupported()
            }

            "openUVCCamera" -> {
                mUVCCameraViewFactory.openUVCCamera()
            }

            "takePicture" -> {
                mUVCCameraViewFactory.takePicture(
                    object : UVCStringCallback {
                        override fun onSuccess(path: String) {
                            result.success(path)
                        }
                        override fun onError(error: String) {
                            result.error("error", error, error)
                        }
                    }
                )
            }

            "zoomIn" -> {
                mUVCCameraViewFactory.zoomIn()
            }

            "zoomOut" -> {
                mUVCCameraViewFactory.zoomOut()
            }

            "setZoomLevel" -> {
                Log.d("UVCCameraView", "setZoomLevel method is being called")
                val zoomLevel = call.argument<Double>("zoomLevel")?.toFloat() ?: 1.0f
                mUVCCameraViewFactory.setZoomLevel(zoomLevel)
                result.success(null)
            }

//            "setZoomLevel" -> {
//                val zoomLevel = call.argument<Double>("zoomLevel") ?: 1.0
//                mUVCCameraViewFactory.setZoomLevel(zoomLevel.toFloat())
//            }

            "captureVideo" -> {
                mUVCCameraViewFactory.captureVideo(
                    object : UVCStringCallback {
                        override fun onSuccess(path: String) {
                            result.success(path)
                        }
                        override fun onError(error: String) {
                            result.error("error", error, error)
                        }
                    }
                )
            }
            "captureStreamStart" -> {
                mUVCCameraViewFactory.captureStreamStart()
            }
            "captureStreamStop" -> {
                mUVCCameraViewFactory.captureStreamStop()
            }

            "closeCamera" -> {
                mUVCCameraViewFactory.closeCamera()
            }


            "getAllPreviewSizes" -> {
                result.success(mUVCCameraViewFactory.getAllPreviewSizes())
            }

            "getCurrentCameraRequestParameters" -> {
                result.success(mUVCCameraViewFactory.getCurrentCameraRequestParameters())
            }

            "updateResolution" -> {
                mUVCCameraViewFactory.updateResolution(call.arguments())
            }

            "getPlatformVersion" -> {
                result.success("Android " + Build.VERSION.RELEASE)
            }

            else -> {
                result.notImplemented()
            }
        }

    }
}
