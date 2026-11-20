package com.campus.schedule.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.campus.schedule.R

/** 通知渠道与通知发送的辅助。 */
object NotificationHelper {

    const val CHANNEL_ID = "class_reminder"
    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_LOCATION = "extra_location"
    const val EXTRA_START = "extra_start"

    /** 创建课前提醒通知渠道（幂等）。 */
    fun ensureChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notification_channel_desc)
        }
        manager.createNotificationChannel(channel)
    }

    /** 发送一条课前提醒。 */
    fun notify(context: Context, id: Long, title: String, location: String, start: String) {
        ensureChannel(context)
        val text = context.getString(R.string.notification_body, start, location, title)
        val notification = android.app.Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setContentTitle(context.getString(R.string.notification_title, title))
            .setContentText(text)
            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(id.toInt(), notification)
    }
}
