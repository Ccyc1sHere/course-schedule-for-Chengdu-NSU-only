package com.campus.schedule.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.campus.schedule.MainActivity
import com.campus.schedule.ScheduleApp
import com.campus.schedule.domain.logic.ReminderCalculator
import com.campus.schedule.domain.logic.WeekCalculator
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.first

/**
 * 2×2「今日课程」小组件（Glance）：显示下一节课名称 + 时间 + 倒计时。
 * 数据来自 Room（repository）；Glance 默认配色自动适配系统深浅主题。
 */
class TodayScheduleWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val app = context.applicationContext as ScheduleApp
        val repository = app.container.repository
        val courses = repository.courses.first()
        val semester = repository.semester.first()

        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek.value
        val week = semester?.let { WeekCalculator.currentWeek(it, today) } ?: 1
        val nowMinute = LocalTime.now().let { it.hour * 60 + it.minute }
        val todayItems = courses.filter { it.dayOfWeek == dayOfWeek && it.isEffectiveOn(week) }
        val next = ReminderCalculator.nextUp(todayItems, nowMinute)

        val title = next?.let { it.item.title } ?: "—"
        val meta = next?.let {
            listOf(it.item.startTimeText, it.item.location).filter { p -> p.isNotBlank() }
                .joinToString(" · ")
        } ?: context.getString(com.campus.schedule.R.string.widget_no_course)
        val countdown = next?.let {
            if (it.ongoing) {
                context.getString(com.campus.schedule.R.string.countdown_ongoing, it.minutesUntil)
            } else {
                context.getString(com.campus.schedule.R.string.countdown_next, it.minutesUntil)
            }
        } ?: ""

        val openIntent = actionStartActivity(Intent(context, MainActivity::class.java))

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .appWidgetBackground()
                    .cornerRadius(16.dp)
                    .clickable(openIntent)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = context.getString(com.campus.schedule.R.string.widget_title),
                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp)
                )
                Text(
                    text = title,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = GlanceModifier.fillMaxWidth().padding(top = 8.dp)
                )
                Text(
                    text = meta,
                    style = TextStyle(fontSize = 13.sp, textAlign = TextAlign.Center),
                    modifier = GlanceModifier.fillMaxWidth().padding(top = 4.dp)
                )
                Text(
                    text = countdown,
                    style = TextStyle(fontSize = 13.sp, textAlign = TextAlign.Center),
                    modifier = GlanceModifier.fillMaxWidth().padding(top = 4.dp)
                )
            }
        }
    }
}
