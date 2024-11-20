import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';

class USBCameraController extends GetxController{

  static const String _channelName = "flutter_uvc_camera/channel";
  MethodChannel? _cameraChannel;


  USBCameraController() {
    _cameraChannel = const MethodChannel(_channelName);
    _cameraChannel?.setMethodCallHandler(_methodChannelHandler);
    debugPrint("------> UVCCameraController init");
  }

  RxDouble currentZoomLevel = 1.0.obs;

  Future<void> _methodChannelHandler(MethodCall call) async {
    switch (call.method) {
      case "callFlutter":
        debugPrint('------> Received from Android：${call.arguments}');
        // _callStrings.add(call.arguments.toString());
        // msgCallback?.call(call.arguments['msg']);

        break;
      case "takePictureSuccess":
        // _takePictureSuccess(call.arguments);
        break;
      case "CameraState":
        // _setCameraState(call.arguments.toString());
        break;
      case "onEncodeData":
        final Map<dynamic, dynamic> args = call.arguments;
        // capture H264 & AAC only
        debugPrint(args.toString());
        break;
    }
  }

  Future<void> initializeCamera() async {
    await _cameraChannel?.invokeMethod('initializeCamera');
    await openUVCCamera();
    startCamera();
  }

  Future<void> openUVCCamera() async {
    debugPrint("openUVCCamera");
    await _cameraChannel?.invokeMethod('openUVCCamera');
  }

  void startCamera() async {
    await _cameraChannel?.invokeMethod('startCamera');
  }

  Future<void> takePicture() async {
    try {
      // Call the native method to capture a picture
      final String imagePath = await _cameraChannel!.invokeMethod('takePicture');
      debugPrint("Picture captured: $imagePath");
      // await saveImageToInternalStorage(imagePath);
    } on PlatformException catch (e) {
      debugPrint("Error capturing picture: ${e.message}");
    }
  }

  Future<void> setZoom(double zoomLevel) async {
    try {
      await _cameraChannel!.invokeMethod('zoomIn');
    } on PlatformException catch (e) {
      debugPrint("Error setting zoom: ${e.message}");
    }
  }

  Future<void> setZoomLevel(double zoomLevel) async {
    try {
      await _cameraChannel!.invokeMethod('setZoomLevel', {"zoomLevel": zoomLevel});
    } on PlatformException catch (e) {
      print("Failed to set zoom level: '${e.message}'.");
    }
  }

}