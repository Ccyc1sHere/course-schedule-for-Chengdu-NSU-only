package com.campus.schedule.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.campus.schedule.ScheduleApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * 开机重启后重排课前提醒（闹钟在重启后失效）。
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val app = context.applicationContext as? ScheduleApp ?: return
        val repository = app.container.repository
        val settings = runBlocking { repository.settings.first() }
        if (!settings.reminderEnabled) return
        val items = runBlocking { repository.courses.first() }
        val semester = runBlocking { repository.semester.first() } ?: return
        ReminderScheduler.schedule(
            context, items, semester,
            settings.reminderMinutesBefore
        )
    }
}
