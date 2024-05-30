package cn.recommender.androiddevtoolbox.base

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import cn.recommender.androiddevtoolbox.Constants
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.ui.activity.MainActivity

abstract class BaseForegroundService : BaseService() {

    data class NotificationData(
        val id: Int,
        val title: String,
        val content: String,
    )

    abstract fun getNotificationData(): NotificationData


    override fun onCreate() {
        super.onCreate()
        makeForeground()
    }

    private fun makeForeground() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channelName = getString(R.string.foreground_service)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = getString(R.string.foreground_service_description)
                }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification: Notification =
            NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE)
                .setContentTitle(getNotificationData().title)
                .setContentText(getNotificationData().content)
                .setSmallIcon(R.drawable.app_logo_round)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .build()

//        ServiceCompat.startForeground(
//            this,
//            getNotificationData().id,
//            notification,
//            getServiceType()
//        )
        startForeground(getNotificationData().id, notification)
    }

//    abstract fun getServiceType(): Int


}