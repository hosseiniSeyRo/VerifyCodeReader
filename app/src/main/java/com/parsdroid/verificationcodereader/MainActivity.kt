package com.parsdroid.verificationcodereader

import android.Manifest.permission.RECEIVE_SMS
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.view.isVisible
import com.parsdroid.verificationcodereader.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mainViewModel: MainViewModel

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    private var buttonNoPermissionClicked = false
    private var neverAskAgainChecked = false
    private val requestPermissionLauncher = registerForActivityResult(
        RequestPermission()
    ) { isGranted: Boolean ->
        binding.groupNoPermission.isVisible = !isGranted
        if (!isGranted) {
            binding.groupNoPermission.isVisible = true
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
        observeViewModel()
    }

    override fun onStart() {
        super.onStart()
        requestSmsPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.removeClickListeners()
        _binding = null
        requestPermissionLauncher.unregister()
    }

    private fun initUI() {
        _binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            setClickListeners()
        }
    }

    private fun observeViewModel() {
        mainViewModel.verificationCode.observe(this@MainActivity) {
            binding.textCode.text = it
        }
    }

    private fun requestSmsPermission() {
        requestPermissionLauncher.launch(RECEIVE_SMS)
    }

    private fun ActivityMainBinding.setClickListeners() {
        textCode.setOnClickListener(mainViewModel::onCodeClicked)
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