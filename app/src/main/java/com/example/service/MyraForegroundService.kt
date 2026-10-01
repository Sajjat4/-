package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class MyraForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "myra_foreground_service_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_SYNC_LIFECYCLE = "com.example.action.SYNC_LIFECYCLE"
        const val EXTRA_LIFECYCLE_STATE = "extra_lifecycle_state"

        var isRunning: Boolean = false
            private set

        fun start(context: Context) {
            val intent = Intent(context, MyraForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, MyraForegroundService::class.java)
            context.stopService(intent)
        }

        fun syncLifecycleState(context: Context, state: String) {
            if (isRunning) {
                val intent = Intent(context, MyraForegroundService::class.java).apply {
                    action = ACTION_SYNC_LIFECYCLE
                    putExtra(EXTRA_LIFECYCLE_STATE, state)
                }
                context.startService(intent)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_SYNC_LIFECYCLE) {
            val state = intent.getStringExtra(EXTRA_LIFECYCLE_STATE) ?: "active"
            val text = when (state) {
                "resumed" -> "MYRA ফোরগ্রাউন্ডে সক্রিয় রয়েছে"
                "paused", "stopped" -> "MYRA ব্যাকগ্রাউন্ডে স্বয়ংক্রিয় সেবা দিচ্ছে"
                else -> "বাংলা ভয়েস সহকারী ও অটোনোমাস এজেন্ট প্রস্তুত"
            }
            val notification = buildNotification(text)
            val manager = getSystemService(NotificationManager::class.java)
            manager.notify(NOTIFICATION_ID, notification)
            return START_STICKY
        }

        val notification = buildNotification("বাংলা ভয়েস সহকারী ও অটোনোমাস এজেন্ট প্রস্তুত")
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MYRA AI Foreground Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "MYRA সার্বক্ষণিক ভয়েস ও অটোনোমাস এজেন্ট সক্রিয় রয়েছে"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(statusText: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MYRA AI সার্বক্ষণিক সক্রিয়")
            .setContentText(statusText)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
