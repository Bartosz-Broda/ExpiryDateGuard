package com.bbroda.expirydateguard.ui.classes

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.activities.MainMenuActivity

object NotificationHandler {
    private const val CHANNEL_ID = "transactions_reminder_channel"

    fun createReminderNotification(context: Context, textContent: String, ID:Int) {
        val TAG = "X"
        Log.d(TAG, "createReminderNotification: CREATE NOTIFICATION ")
        //  No back-stack when launched
        val intent = Intent(context, MainMenuActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        createNotificationChannel(context) // This won't create a new channel everytime, safe to call

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon_chicken3)
            .setContentTitle("Expiry Date Guard")
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // For launching the MainActivity
            .setAutoCancel(true) // Remove notification when tapped
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)// Show on lock screen
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(textContent)) //Show longer text

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(ID, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        val name = "ExpiryChannel"
        val descriptionText = "Notification appearing when one of grocery products is close to be expired"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}