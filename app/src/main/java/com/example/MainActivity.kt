package com.example

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.permission.MyraPermissionManager
import com.example.service.MyraForegroundService
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.BongoLiveTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MYRA_MainActivity"
    }

    private val viewModel: MainViewModel by viewModels()
    lateinit var permissionManager: MyraPermissionManager
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Launching 100% Native Jetpack Compose MYRA Application")

        window.decorView.setBackgroundColor(Color.parseColor("#09090B"))

        // Initialize Native Permission Manager
        permissionManager = MyraPermissionManager(this)

        // Set Native Jetpack Compose UI
        setContent {
            BongoLiveTheme {
                MainAppScreen(
                    viewModel = viewModel,
                    onOpenAccessibilitySettings = {
                        permissionManager.openAccessibilitySettings()
                    }
                )
            }
        }

        // Trigger first-launch educational permission & accessibility check
        permissionManager.checkAndRequestPermissionsOnStartup { granted ->
            if (granted) {
                MyraForegroundService.start(this)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Synchronizing MYRA states")

        if (::permissionManager.isInitialized && permissionManager.isMicrophoneGranted()) {
            MyraForegroundService.start(this)
            MyraForegroundService.syncLifecycleState(this, "started")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Bringing MYRA to foreground")
        MyraForegroundService.syncLifecycleState(this, "resumed")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Moving MYRA to background")
        MyraForegroundService.syncLifecycleState(this, "paused")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Background execution state active")
        MyraForegroundService.syncLifecycleState(this, "stopped")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: Terminating activity context")
        super.onDestroy()
    }
}
