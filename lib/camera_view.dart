import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:usb_camera/camera_controller.dart';

class CameraView extends StatefulWidget {
  const CameraView({super.key});

  @override
  State<CameraView> createState() => _CameraViewState();
}

class _CameraViewState extends State<CameraView> {

  USBCameraController usbCameraController = Get.put(USBCameraController());
@override
  void initState() {
  Permission.camera.request();
  print(Permission.camera.status);
    super.initState();
  }
  double _currentZoom = 1.0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          Expanded(
            child: GestureDetector(
              onScaleUpdate: (details) {
                setState(() {
                  _currentZoom = details.scale.clamp(1.0, 4.0); // Limit zoom between 1x and 4x
                });
                usbCameraController.setZoom(_currentZoom);
              },
              child: AndroidView(
                viewType: 'uvc_camera_view',
                  onPlatformViewCreated: (id) {
                    usbCameraController.initializeCamera();
                  }
              ),
            ),
          ),
          Obx(() {
              return Slider(
                min: 1.0,
                max: 50.0, // example max zoom level
                value: usbCameraController.currentZoomLevel.value,
                onChanged: (double value) {
                  // setState(() {
                    // _currentZoomLevel = value;
                  // });
                  usbCameraController.currentZoomLevel.value = value;
                  usbCameraController.setZoomLevel(value);
                },
              );
            }
          ),
          ElevatedButton(
            onPressed: () async {
              await usbCameraController.takePicture();
            },
            child: Text("Capture Picture"),
          ),
        ],
      ),
    );
  }
}
