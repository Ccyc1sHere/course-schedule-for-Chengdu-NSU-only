package com.campus.schedule.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.campus.schedule.domain.logic.ReminderCalculator
import com.campus.schedule.domain.logic.WeekCalculator
import com.campus.schedule.domain.model.CourseItem
import com.campus.schedule.domain.model.SemesterConfig
import java.time.LocalDate

/**
 * 课前提醒调度：使用 AlarmManager 精确闹钟。
 * 触发时刻由 [ReminderCalculator.upcomingTriggers] 纯函数计算，这里只负责登记系统闹钟。
 */
object ReminderScheduler {

    /** 为未来 [lookaheadDays] 天内的生效日程排提醒。 */
    fun schedule(
        context: Context,
        items: List<CourseItem>,
        semester: SemesterConfig,
        minutesBefore: Int,
        lookaheadDays: Int = 14
    ) {
        NotificationHelper.ensureChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggers = ReminderCalculator.upcomingTriggers(
            items = items,
            fromDate = LocalDate.now(),
            weekOfDate = { WeekCalculator.weekOf(semester, it) },
            minutesBefore = minutesBefore,
            lookaheadDays = lookaheadDays
        )
        triggers.forEach { (item, epochMillis) ->
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                epochMillis,
                buildIntent(context, item)
            )
        }
    }

    /** 取消全部提醒（用户关闭课前提醒时调用）。 */
    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = PendingIntent.getBroadcast(
            context, 0, Intent(context, ReminderReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pi?.let { alarmManager.cancel(it) }
    }

    private fun buildIntent(context: Context, item: CourseItem): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(NotificationHelper.EXTRA_TITLE, item.title)
            putExtra(NotificationHelper.EXTRA_LOCATION, item.location)
            putExtra(NotificationHelper.EXTRA_START, item.startTimeText)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(
            context,
            item.id.toInt(),
            intent,
            flags
        )
    }
}
