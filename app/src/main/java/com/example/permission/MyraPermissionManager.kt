package com.example.permission

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.service.MyraAccessibilityService

/**
 * MYRA Native Permission Manager
 * Detects, handles, and educates users regarding required Android permissions:
 * - Microphone (Audio recording for Gemini Live Bengali conversation)
 * - Notifications (Persistent Foreground Service notifications)
 * - Accessibility Service (Autonomous screen inspection and UI automation)
 * - System Alert Window / Overlay (Floating voice assistant controls)
 */
class MyraPermissionManager(private val activity: ComponentActivity) {

    companion object {
        private const val PREFS_NAME = "myra_permission_prefs"
        private const val KEY_FIRST_LAUNCH = "is_first_launch_done"
        private const val KEY_ACCESSIBILITY_PROMPTED = "accessibility_prompted"
    }

    private val prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null
    private var onPermissionsResultCallback: ((Boolean) -> Unit)? = null

    init {
        permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsMap ->
            val micGranted = permissionsMap[Manifest.permission.RECORD_AUDIO] ?: isMicrophoneGranted()
            val notifGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionsMap[Manifest.permission.POST_NOTIFICATIONS] ?: isNotificationsGranted()
            } else true

            // Trigger callback with status
            onPermissionsResultCallback?.invoke(micGranted && notifGranted)

            // After runtime permissions are dealt with, check Accessibility Service
            if (!isAccessibilityServiceEnabled()) {
                showAccessibilityEducationalDialog()
            }
        }
    }

    fun isFirstLaunch(): Boolean {
        return !prefs.getBoolean(KEY_FIRST_LAUNCH, false)
    }

    fun markFirstLaunchDone() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, true).apply()
    }

    fun isMicrophoneGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isNotificationsGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun isAccessibilityServiceEnabled(): Boolean {
        return MyraAccessibilityService.instance != null
    }

    fun isOverlayPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(activity)
        } else {
            true
        }
    }

    /**
     * Checks all critical permissions and triggers educational setup flow if missing.
     */
    fun checkAndRequestPermissionsOnStartup(onComplete: ((Boolean) -> Unit)? = null) {
        this.onPermissionsResultCallback = onComplete

        val needsMic = !isMicrophoneGranted()
        val needsNotif = !isNotificationsGranted()
        val needsAccessibility = !isAccessibilityServiceEnabled()

        if (isFirstLaunch() || needsMic || needsNotif || needsAccessibility) {
            showEducationalSetupDialog {
                requestRuntimePermissions()
            }
            markFirstLaunchDone()
        } else {
            onComplete?.invoke(true)
        }
    }

    /**
     * Shows an educational onboarding prompt explaining WHY each permission is necessary for MYRA.
     */
    private fun showEducationalSetupDialog(onProceed: () -> Unit) {
        val message = StringBuilder().apply {
            append("MYRA একটি অটোনোমাস এআই ভয়েস ও স্ক্রিন সহকারী। পূর্ণাঙ্গ সেবার জন্য নিম্নোক্ত অনুমতিসমূহ প্রয়োজন:\n\n")
            append("🎙️ মাইক্রোফোন: সাবলীল বাংলা ভয়েস কমান্ড ও রিয়েল-টাইম জেমিনাই লাইভ কথোপকথনের জন্য।\n\n")
            append("🔔 নোটিফিকেশন: অ্যাপ মিনিমাইজ থাকলেও ব্যাকগ্রাউন্ডে সার্ভিস চালু রাখার জন্য।\n\n")
            append("⚡ অ্যাক্সেসিবিলিটি সার্ভিস: আপনার নির্দেশে স্ক্রিন পর্যবেক্ষণ ও অন্যান্য অ্যাপ (WhatsApp, YouTube) স্বয়ংক্রিয়ভাবে পরিচালনার জন্য।\n\n")
            append("আপনি কি অনুমতি প্রদান করতে প্রস্তুত?")
        }.toString()

        AlertDialog.Builder(activity)
            .setTitle("MYRA অনুমতি ও সেটআপ")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("অনুমতি দিন") { dialog, _ ->
                dialog.dismiss()
                onProceed()
            }
            .setNegativeButton("পরে করব") { dialog, _ ->
                dialog.dismiss()
                onPermissionsResultCallback?.invoke(false)
            }
            .show()
    }

    /**
     * Requests runtime permissions (Microphone, Notifications).
     */
    fun requestRuntimePermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (!isMicrophoneGranted()) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isNotificationsGranted()) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher?.launch(permissionsToRequest.toTypedArray())
        } else {
            if (!isAccessibilityServiceEnabled()) {
                showAccessibilityEducationalDialog()
            } else {
                onPermissionsResultCallback?.invoke(true)
            }
        }
    }

    /**
     * Shows educational prompt specifically before directing the user to Android Accessibility Settings.
     */
    fun showAccessibilityEducationalDialog() {
        AlertDialog.Builder(activity)
            .setTitle("অ্যাক্সেসিবিলিটি সার্ভিস সক্রিয় করুন")
            .setMessage("MYRA অটোনোমাস এজেন্ট দিয়ে সরাসরি স্ক্রিন দেখে WhatsApp বা YouTube পরিচালনা করতে Android Accessibility Settings থেকে 'MYRA' চালু করুন।\n\n'সেটিংস খুলুন' বাটনে চাপ দিয়ে 'Installed Apps' বা 'MYRA' সিলেক্ট করে অন করুন।")
            .setPositiveButton("সেটিংস খুলুন") { dialog, _ ->
                dialog.dismiss()
                openAccessibilitySettings()
            }
            .setNegativeButton("পরে করব") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun openAccessibilitySettings() {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            activity.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to system settings
            activity.startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    fun openOverlaySettings() {
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
                // ignore
            }
        }
    }
}
