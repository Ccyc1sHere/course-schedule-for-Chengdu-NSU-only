package com.campus.schedule.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.campus.schedule.ScheduleApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * 课前提醒广播接收器：触发时发送通知；若用户开启「上课自动勿扰」，进入勿扰模式。
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(NotificationHelper.EXTRA_TITLE) ?: return
        val location = intent.getStringExtra(NotificationHelper.EXTRA_LOCATION) ?: ""
        val start = intent.getStringExtra(NotificationHelper.EXTRA_START) ?: ""
        NotificationHelper.notify(
            context,
            System.currentTimeMillis() % Int.MAX_VALUE,
            title, location, start
        )
        maybeEnableDnd(context)
    }

    /** 若用户开启「上课自动勿扰」，切换系统勿扰（静音）。 */
    private fun maybeEnableDnd(context: Context) {
        val app = context.applicationContext as? ScheduleApp ?: return
        val settings = runBlocking { app.container.repository.settings.first() }
        if (!settings.dndEnabled) return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (nm.isNotificationPolicyAccessGranted) {
            nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
        }
    }
}
