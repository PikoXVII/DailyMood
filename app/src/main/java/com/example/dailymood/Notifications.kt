package com.example.dailymood

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

// Notifikationskanal-ID (namngiven konstant = ej magic number)
private const val CHANNEL_ID = "daily_mood_channel"

// Notis-ID för att identifiera vår testnotis
private const val NOTIFICATION_ID = 1001 // vilket som helst, bara inte “1”

// Android 13 (API 33) – ersätter magic number 33
private const val ANDROID_13_API = Build.VERSION_CODES.TIRAMISU

// Skapar kanal vid behov (Android 8+)
private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "DailyMood reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Reminders to log today's mood"
        }

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}

// Skickar en enkel notis – men bara om vi har rätt permission
fun showTestNotification(context: Context) {

    // Android 13+ kräver POST_NOTIFICATIONS-permission
    if (Build.VERSION.SDK_INT >= ANDROID_13_API) {

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            // Permission saknas – gör inget
            return
        }
    }

    createNotificationChannel(context)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("DailyMood")
        .setContentText("Don't forget to log today's mood!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        notify(NOTIFICATION_ID, builder.build())
    }
}
