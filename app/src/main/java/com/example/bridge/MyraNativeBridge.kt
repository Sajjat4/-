package com.example.bridge

import android.Manifest
import android.app.Activity
import com.example.MainActivity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.service.MyraAccessibilityService
import com.example.service.MyraForegroundService

class MyraNativeBridge(
    private val activity: Activity
) {

    @JavascriptInterface
    fun getSetupStatus(): String {
        val hasMic = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        val hasNotifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true

        val hasAccessibility = MyraAccessibilityService.instance != null
        val hasOverlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(activity)
        } else true

        val json = org.json.JSONObject().apply {
            put("microphone", hasMic)
            put("notifications", hasNotifications)
            put("accessibility", hasAccessibility)
            put("overlay", hasOverlay)
        }
        return json.toString()
    }

    @JavascriptInterface
    fun requestPermissionsFlow() {
        activity.runOnUiThread {
            if (activity is MainActivity) {
                activity.permissionManager.requestRuntimePermissions()
            }
        }
    }

    @JavascriptInterface
    fun isAccessibilityServiceEnabled(): Boolean {
        return MyraAccessibilityService.instance != null
    }

    @JavascriptInterface
    fun openAccessibilitySettings() {
        activity.runOnUiThread {
            try {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                activity.startActivity(intent)
            } catch (e: Exception) {
                showToast("সেটিংস খুলতে ব্যর্থ: ${e.message}")
            }
        }
    }

    @JavascriptInterface
    fun openOverlaySettings() {
        activity.runOnUiThread {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${activity.packageName}")
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    activity.startActivity(intent)
                } catch (e: Exception) {
                    showToast("ওভারলে সেটিংস খুলতে ব্যর্থ: ${e.message}")
                }
            }
        }
    }

    @JavascriptInterface
    fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @JavascriptInterface
    fun inspectScreen(): String {
        val service = MyraAccessibilityService.instance
        return service?.inspectCurrentScreen() ?: "[]"
    }

    @JavascriptInterface
    fun performClick(x: Float, y: Float): Boolean {
        val service = MyraAccessibilityService.instance ?: return false
        return service.dispatchClick(x, y)
    }

    @JavascriptInterface
    fun performClickOnNode(viewId: String?, text: String?): Boolean {
        val service = MyraAccessibilityService.instance ?: return false
        return service.performClickOnNode(viewId, text)
    }

    @JavascriptInterface
    fun performInputText(text: String): Boolean {
        val service = MyraAccessibilityService.instance ?: return false
        return service.performInputText(text)
    }

    @JavascriptInterface
    fun performScroll(direction: String): Boolean {
        val service = MyraAccessibilityService.instance ?: return false
        return service.performScroll(direction)
    }

    @JavascriptInterface
    fun performGlobalAction(action: String): Boolean {
        val service = MyraAccessibilityService.instance ?: return false
        return service.performGlobalActionCompat(action)
    }

    @JavascriptInterface
    fun launchApp(packageName: String): Boolean {
        val pm = activity.packageManager
        val intent = pm.getLaunchIntentForPackage(packageName) ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
        return true
    }

    @JavascriptInterface
    fun startForegroundService() {
        MyraForegroundService.start(activity)
    }

    @JavascriptInterface
    fun stopForegroundService() {
        MyraForegroundService.stop(activity)
    }

    @JavascriptInterface
    fun showToast(msg: String) {
        activity.runOnUiThread {
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    @JavascriptInterface
    fun triggerHaptic(effect: String) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = activity.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val ve = when (effect.lowercase()) {
                    "heavy" -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                    "double" -> VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 50), -1)
                    else -> VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE)
                }
                vibrator.vibrate(ve)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(40)
            }
        } catch (e: Exception) {
            // Ignore vibration error
        }
    }
}
