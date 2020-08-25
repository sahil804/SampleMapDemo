package com.example.samplemapdemo

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

fun Context.hasPermission(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(this, permission) ==
        PackageManager.PERMISSION_GRANTED
}

fun Fragment.requestPermissionWithRationale(
    permission: String,
    requestCode: Int
) {
    val provideRationale = shouldShowRequestPermissionRationale(permission)

    if (!provideRationale) {
        requestPermissions(arrayOf(permission), requestCode)
    }
}

fun Context.isLocationEnabled(): Boolean {
    val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var gpsEnabled = false

    try {
        gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    } catch (ex: Exception) {
    }
    return gpsEnabled
}
