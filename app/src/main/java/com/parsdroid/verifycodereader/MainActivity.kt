package com.parsdroid.verifycodereader

import android.Manifest.permission.RECEIVE_SMS
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.view.isVisible
import com.parsdroid.verifycodereader.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    private var buttonNoPermissionClicked = false
    private var neverAskAgainChecked = false
    private val requestPermissionLauncher = registerForActivityResult(
        RequestPermission()
    ) { isGranted: Boolean ->
        binding?.groupNoPermission?.isVisible = !isGranted
        if (!isGranted) {
            binding?.groupNoPermission?.isVisible = true
            if (neverAskAgainChecked &&
                !shouldShowRequestPermissionRationale(this, RECEIVE_SMS) &&
                buttonNoPermissionClicked
            ) {
                buttonNoPermissionClicked = false
                openAppInfo()
            }
            neverAskAgainChecked = !shouldShowRequestPermissionRationale(this, RECEIVE_SMS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    override fun onStart() {
        super.onStart()
        requestSmsPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding?.removeClickListeners()
        binding = null
        requestPermissionLauncher.unregister()
    }

    private fun initUI() {
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            setClickListeners()
        }
    }

    private fun requestSmsPermission() {
        requestPermissionLauncher.launch(RECEIVE_SMS)
    }

    private fun ActivityMainBinding.setClickListeners() {
        textCode.setOnClickListener {
            copyToClipboard(textCode.text)
        }
        buttonNoPermission.setOnClickListener {
            buttonNoPermissionClicked = true
            requestSmsPermission()
        }
    }

    private fun ActivityMainBinding.removeClickListeners() {
        buttonNoPermission.setOnClickListener(null)
        textCode.setOnClickListener(null)
    }
}