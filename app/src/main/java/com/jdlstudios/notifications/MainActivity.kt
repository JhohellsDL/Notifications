package com.jdlstudios.notifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jdlstudios.notifications.DataResetReceiver.Companion.NOTIFICATION_ID
import com.jdlstudios.notifications.DataResetReceiver.Companion.NOTIFICATION_ID_1
import com.jdlstudios.notifications.databinding.ActivityMainBinding
import java.util.Calendar

private const val ELAPSED_SECONDS = 3000

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)


        binding.buttonNotificationBasic.setOnClickListener {
            createNotificationChannel()
            scheduleNotification(1)
        }
        binding.buttonNotificationProgressBar.setOnClickListener{
            createNotificationChannel1()
            scheduleNotification1(2)
        }

        setContentView(binding.root)

    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification1(type: Int) {
        val intent = Intent(applicationContext, DataResetReceiver::class.java)
        intent.apply {
            putExtra("clave", type)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            NOTIFICATION_ID_1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().timeInMillis + ELAPSED_SECONDS,
            pendingIntent
        )

    }
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(type: Int) {
        val intent = Intent(applicationContext, DataResetReceiver::class.java)
        intent.apply {
            putExtra("clave", type)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().timeInMillis + ELAPSED_SECONDS,
            pendingIntent
        )

    }

    private fun createNotificationChannel() {
        val channelId = "myChannel" // Identificador del canal

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "MySuperChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "ASDASDASD"
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun createNotificationChannel1() {
        val channelId = "myChannel1" // Identificador del canal

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "MySuperChannel1",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "ASDASDASD"
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}