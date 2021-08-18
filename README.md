
  
    
# Preview    

[Video Demo](https://downloads.jamesswinton.com/apks/POCs/GeofenceMonitorPOC/Geofence%20Monitoring%20POC.mp4)

![Preview](https://downloads.jamesswinton.com/apks/POCs/GeofenceMonitorPOC/capture.png)    
![Preview](https://downloads.jamesswinton.com/apks/POCs/GeofenceMonitorPOC/capture-1.png)   
  
# Description 
Creates & monitors up-to 100 use-defined Geofences. Uses standard Android Geofencing APIs. When entering a defined Geofence a dialog will be displayed with a user-defined message. Additionally, a notification will also be sent with the same information. When the Geofence is exited, if the dialog is still present on the screen it will be removed automatically. The Notification remains as a historical log of the warnings and require the user to manually remove them.  
    
# Setup 
On first install the app will copy a geofence_config.json file to the /sdcard/android/data/com.zebra.jamesswinton.geofencemonitorpoc/files/ directory. You can add / remove Geofences to / from this file and they will be loaded with an application restart.   
  
There is a sample of this JSON file below for convenience, just follow the existing structure to add up-to 100 Geofences.  
  
Alternatively, you can use the UI to add / remove Geofences. Any changes will be saved to the geofence_config.json file when the monitoring service is launched.  

The debug release APK is compatible with Mock Locations for testing - download link below. 
    
# Geofence Config Sample    
 ``` 
[
   {
      "label":"8 Salters Way",
      "desc":"Dangerous Dog at 8 Salters Way",
      "lat":51.89666599170621,
      "lng":-0.539039878704451,
      "radius":50
   },
   {
      "label":"Ravenscourt",
      "desc":"Aggresive Customer at Ravenscourt",
      "lat":51.89761797484031,
      "lng":-0.5389045511187073,
      "radius":50
   },
   {
      "label":"14 Barrie Avenue",
      "desc":"Dangerous Dog at 14 Barrie Avenue",
      "lat":51.89741199189826,
      "lng":-0.5416742557413684,
      "radius":50
   }
]  
```    

 # Pre-compiled 
 [Download Release APK](https://downloads.jamesswinton.com/apks/POCs/GeofenceMonitorPOC/GeofenceMonitorPOC-release.apk)   
[Download Debug APK](https://downloads.jamesswinton.com/apks/POCs/GeofenceMonitorPOC/GeofenceMonitorPOC-debug.apk)   
    
# Stage Now Barcode 
![SN Barcode](https://downloads.jamesswinton.com/apks/POCs/GeofenceMonitorPOC/snbarcode.png)
