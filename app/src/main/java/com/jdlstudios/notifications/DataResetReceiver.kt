package com.jdlstudios.notifications

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BADGE_ICON_LARGE
import androidx.core.app.NotificationCompat.PRIORITY_LOW
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import java.util.Timer
import java.util.TimerTask

class DataResetReceiver : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_ID_1 = 1
        const val CHANNEL_ID = "myChannel"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val type = intent?.getIntExtra("clave", 0)
        type?.let { createNotification(context, it) }
    }

    fun createNotification(context: Context, type: Int) {
        when (type) {
            1 -> {
                createSimpleNotification(context)
            }

            2 -> {
                createProgressBarNotification(context)
            }
        }
    }

    private fun createSimpleNotification(context: Context) {

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(
            context, CHANNEL_ID
        ).apply {
            setBadgeIconType(BADGE_ICON_LARGE)
            setSmallIcon(R.drawable.rimac)
            setContentTitle("Rimac App")
            setContentText("Notificacion basica")
            setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Esta notificacion es simple y basica para probar")
            )
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(pendingIntent)
            setAutoCancel(true)
            setVisibility(VISIBILITY_PUBLIC)
        }


        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.apply {
            notify(NOTIFICATION_ID, notification.build())
        }

    }

    private fun createProgressBarNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(
            context, "myChannel1"
        ).apply {
            setBadgeIconType(BADGE_ICON_LARGE)
            setSmallIcon(R.drawable.rimac)
            priority = PRIORITY_LOW
            setContentIntent(pendingIntent)
            setAutoCancel(true)
            setVisibility(VISIBILITY_PUBLIC)
        }
        val PROGRESS_MAX = 100
        val PROGRESS_CURRENT = 0

        NotificationManagerCompat.from(context).apply {
            notification.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(NOTIFICATION_ID_1, notification.build())

            val timer = Timer()
            timer.scheduleAtFixedRate(object : TimerTask() {
                var progress = 0

                override fun run() {
                    if (progress <= PROGRESS_MAX) {
                        progress += 1
                        // Update progress and notification
                        notification.setOnlyAlertOnce(true)
                        notification.setProgress(PROGRESS_MAX, progress, false)

                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            return
                        }
                        notify(NOTIFICATION_ID_1, notification.build())
                    } else {
                        // When done, update the notification one more time to remove the progress bar
                        notification.setOnlyAlertOnce(false)
                        notification.setContentText("Download complete")
                            .setProgress(0, 0, false)
                        notify(NOTIFICATION_ID_1, notification.build())
                        // Cancel the timer
                        timer.cancel()
                    }
                }
            }, 0, 100)

            // When done, update the notification once more to remove the progress bar.

        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID_1, notification.build())

    }
}
