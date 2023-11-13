package com.jdlstudios.notifications

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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

            3 -> {
                createProgressBarNotificationCustom(context)
            }

            4 -> {
                createProgressBarNotificationCustomPointsCrane(context)
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
            context, CHANNEL_ID
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
            notify(NOTIFICATION_ID, notification.build())

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
                        notify(NOTIFICATION_ID, notification.build())
                    } else {
                        // When done, update the notification one more time to remove the progress bar
                        notification.setOnlyAlertOnce(false)
                        notification.setContentText("Download complete")
                            .setProgress(0, 0, false)
                        notify(NOTIFICATION_ID, notification.build())
                        // Cancel the timer
                        timer.cancel()
                    }
                }
            }, 0, 100)

            // When done, update the notification once more to remove the progress bar.

        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification.build())

    }

    private fun createProgressBarNotificationCustom(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val progressMax = 100
        val progressCurrent = 0

        val customContentView =
            RemoteViews(context.packageName, R.layout.custom_notification_layout)

        val imageResourceIds = intArrayOf(
            R.id.imagen_0, R.id.imagen_1, R.id.imagen_2, R.id.imagen_3,
            R.id.imagen_4, R.id.imagen_5, R.id.imagen_6, R.id.imagen_7,
            R.id.imagen_8, R.id.imagen_9, R.id.imagen_10, R.id.imagen_11,
            R.id.imagen_12, R.id.imagen_13
        )
        for (i in imageResourceIds.indices) {
            customContentView.setImageViewResource(
                imageResourceIds[i],
                R.drawable.custom_icom_space_transparent
            )
        }
        customContentView.setImageViewResource(
            R.id.imagen_21,
            R.drawable.endpoint
        )
        customContentView.setProgressBar(
            R.id.notificationProgressBar,
            progressMax,
            progressCurrent,
            false
        )

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setBadgeIconType(BADGE_ICON_LARGE)
            setSmallIcon(R.drawable.rimac)
            priority = PRIORITY_LOW
            setContentIntent(pendingIntent)
            setCustomContentView(customContentView)
            setCustomBigContentView(customContentView)
            setAutoCancel(true)
            setVisibility(VISIBILITY_PUBLIC)
        }

        NotificationManagerCompat.from(context).apply {
            notification.setProgress(progressMax, progressCurrent, false)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(NOTIFICATION_ID, notification.build())

            val timer = Timer()
            timer.scheduleAtFixedRate(object : TimerTask() {
                var progress = 0

                override fun run() {
                    if (progress <= progressMax) {
                        progress += 1
                        // Update progress and notification
                        notification.setOnlyAlertOnce(true)
                        notification.setProgress(progressMax, progress, false)
                        customContentView.setProgressBar(
                            R.id.notificationProgressBar,
                            progressMax,
                            progress,
                            false
                        )
                        when (progress) {
                            1 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_0,
                                    R.drawable.camion_grua
                                )
                            }

                            7 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_0,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_1,
                                    R.drawable.camion_grua
                                )
                            }

                            12 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_1,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_2,
                                    R.drawable.camion_grua
                                )
                            }

                            18 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_2,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_3,
                                    R.drawable.camion_grua
                                )
                            }

                            26 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_3,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_4,
                                    R.drawable.camion_grua
                                )
                            }

                            32 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_4,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_5,
                                    R.drawable.camion_grua
                                )
                            }

                            40 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_5,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_6,
                                    R.drawable.camion_grua
                                )
                            }

                            47 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_6,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_7,
                                    R.drawable.camion_grua
                                )
                            }

                            52 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_7,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_8,
                                    R.drawable.camion_grua
                                )
                            }

                            60 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_8,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_9,
                                    R.drawable.camion_grua
                                )
                            }

                            68 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_9,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_10,
                                    R.drawable.camion_grua
                                )
                            }

                            74 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_10,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_11,
                                    R.drawable.camion_grua
                                )
                            }

                            82 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_11,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_12,
                                    R.drawable.camion_grua
                                )
                            }

                            90 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_12,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_13,
                                    R.drawable.camion_grua
                                )
                            }

                            100 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_13,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_21,
                                    R.drawable.endpointred
                                )
                                customContentView.setTextViewText(
                                    R.id.title,
                                    "Alfredo ha llegado a la dirección"
                                )
                                customContentView.setTextViewText(
                                    R.id.subtitle,
                                    "Hemos llegado a la dirección"
                                )
                            }
                        }

                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            return
                        }
                        notify(NOTIFICATION_ID, notification.build())
                    } else {
                        // When done, update the notification one more time to remove the progress bar
                        notification.setOnlyAlertOnce(false)
                        notification.setContentText("Download complete")
                            .setProgress(0, 0, false)
                        notify(NOTIFICATION_ID, notification.build())
                        // Cancel the timer
                        timer.cancel()
                    }
                }
            }, 0, 100)
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification.build())
    }

    private fun createProgressBarNotificationCustomPointsCrane(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val progressMax = 100
        val progressCurrent = 0

        val customContentView =
            RemoteViews(context.packageName, R.layout.custom_notification_layout_points_crane)
        val customContentView2 =
            RemoteViews(context.packageName, R.layout.custom_notification_layout)

        val imageResourceIds = intArrayOf(
            R.id.imagen_0, R.id.imagen_1, R.id.imagen_2, R.id.imagen_3,
            R.id.imagen_4, R.id.imagen_5, R.id.imagen_6, R.id.imagen_7,
            R.id.imagen_8, R.id.imagen_9, R.id.imagen_10, R.id.imagen_11,
            R.id.imagen_12, R.id.imagen_13
        )
        for (i in imageResourceIds.indices) {
            customContentView.setImageViewResource(
                imageResourceIds[i],
                R.drawable.custom_icom_space_transparent
            )
        }
        customContentView.setImageViewResource(
            R.id.imagen_21,
            R.drawable.endpoint
        )
        customContentView.setProgressBar(
            R.id.notificationProgressBar,
            progressMax,
            progressCurrent,
            false
        )

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setBadgeIconType(BADGE_ICON_LARGE)
            setSmallIcon(R.drawable.rimac)
            priority = PRIORITY_LOW
            setContentIntent(pendingIntent)
            setCustomContentView(customContentView)
            setCustomBigContentView(customContentView)
            setAutoCancel(true)
            setVisibility(VISIBILITY_PUBLIC)
        }

        NotificationManagerCompat.from(context).apply {
            notification.setProgress(progressMax, progressCurrent, false)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(NOTIFICATION_ID, notification.build())

            val timer = Timer()
            timer.scheduleAtFixedRate(object : TimerTask() {
                var progress = 0
                var progress2 = 0

                override fun run() {
                    if (progress <= progressMax) {
                        progress += 1

                        // Update progress and notification
                        notification.setOnlyAlertOnce(true)
                        notification.setProgress(progressMax, progress, false)
                        customContentView.setProgressBar(
                            R.id.notificationProgressBar1,
                            50,
                            progress,
                            false
                        )
                        if (progress >= 50){
                            customContentView.setProgressBar(
                                R.id.notificationProgressBar2,
                                50,
                                progress-50,
                                false
                            )
                        }

                        when (progress) {
                            1 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_0,
                                    R.drawable.camion_grua
                                )
                            }

                            6 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_0,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_1,
                                    R.drawable.camion_grua
                                )
                            }

                            15 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_1,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_2,
                                    R.drawable.camion_grua
                                )
                            }

                            23 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_2,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_3,
                                    R.drawable.camion_grua
                                )
                            }

                            31 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_3,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_4,
                                    R.drawable.camion_grua
                                )
                            }

                            39 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_4,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_5,
                                    R.drawable.camion_grua
                                )
                            }

                            47 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_5,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_6,
                                    R.drawable.camion_grua
                                )
                            }

                            50 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_6,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_7,
                                    R.drawable.camion_grua
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_21,
                                    R.drawable.endpointred
                                )
                            }

                            56 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_7,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_8,
                                    R.drawable.camion_grua
                                )
                            }

                            62 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_8,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_9,
                                    R.drawable.camion_grua
                                )
                            }

                            70 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_9,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_10,
                                    R.drawable.camion_grua
                                )
                            }

                            78 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_10,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_11,
                                    R.drawable.camion_grua
                                )
                            }

                            86 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_11,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_12,
                                    R.drawable.camion_grua
                                )
                            }

                            94 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_12,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_13,
                                    R.drawable.camion_grua
                                )
                            }

                            100 -> {
                                customContentView.setImageViewResource(
                                    R.id.imagen_13,
                                    R.drawable.custom_icom_space_transparent
                                )
                                customContentView.setImageViewResource(
                                    R.id.imagen_22,
                                    R.drawable.endpointred
                                )
                                customContentView.setTextViewText(
                                    R.id.title,
                                    "Alfredo ha llegado a la dirección"
                                )
                                customContentView.setTextViewText(
                                    R.id.subtitle,
                                    "Hemos llegado a la dirección"
                                )
                            }
                        }

                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            return
                        }
                        notify(NOTIFICATION_ID, notification.build())
                    } else {
                        // When done, update the notification one more time to remove the progress bar
                        notification.setOnlyAlertOnce(false)
                        notification.setContentText("Download complete")
                            .setProgress(0, 0, false)
                        notify(NOTIFICATION_ID, notification.build())
                        // Cancel the timer
                        timer.cancel()
                    }
                }
            }, 0, 100)
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification.build())
    }
}
