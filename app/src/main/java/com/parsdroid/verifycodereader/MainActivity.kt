package com.parsdroid.verifycodereader

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECEIVE_SMS),
            REQUEST_CODE_SMS_RECEIVE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_SMS_RECEIVE) {
            if ((grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED)) {
                Log.e("RHLog", "MY_PERMISSIONS_REQUEST_SMS_RECEIVE --> YES")
            } else {
                Log.e("RHLog", "MY_PERMISSIONS_REQUEST_SMS_RECEIVE --> NO")
            }
        }
    }

    companion object {

        private const val REQUEST_CODE_SMS_RECEIVE = 10
    }
}