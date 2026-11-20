package com.campus.schedule.domain.logic

import com.campus.schedule.domain.model.CourseItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * 倒计时与通知调度计算（纯函数，便于测试）。
 *
 * 传入的 [now] / [today] 由调用方注入，避免函数内部读取系统时钟导致不可测。
 */
object ReminderCalculator {

    /** 今日课表状态。 */
    enum class TodayStatus { NO_COURSE, ONGOING, UPCOMING, ALL_DONE, OUT_OF_SEMESTER }

    /**
     * 顶部常驻信息：下一节课（或正在进行的一节）及其倒计时。
     * 只看「当周确实生效」的日程，非本周日程不参与倒计时。
     */
    data class NextUp(
        val item: CourseItem,
        val minutesUntil: Int,
        val ongoing: Boolean
    )

    /**
     * 计算下一条日程。
     *
     * @param items 当天的日程（调用方已按当周生效过滤）
     * @param nowMinute 当前时刻的「当日分钟数」
     */
    fun nextUp(items: List<CourseItem>, nowMinute: Int): NextUp? {
        val sorted = items.sortedWith(compareBy({ it.startTime }, { it.id }))
        val ongoing = sorted.firstOrNull { it.startTime <= nowMinute && nowMinute < it.endTime }
        if (ongoing != null) {
            return NextUp(ongoing, ongoing.endTime - nowMinute, ongoing = true)
        }
        val upcoming = sorted.firstOrNull { it.startTime > nowMinute }
            ?: return null
        return NextUp(upcoming, upcoming.startTime - nowMinute, ongoing = false)
    }

    /** 今日状态：无课 / 进行中 / 有待上课程 / 已全部结束。 */
    fun todayStatus(items: List<CourseItem>, nowMinute: Int, inSemester: Boolean): TodayStatus {
        if (!inSemester) return TodayStatus.OUT_OF_SEMESTER
        if (items.isEmpty()) return TodayStatus.NO_COURSE
        val next = nextUp(items, nowMinute)
        return when {
            next == null -> TodayStatus.ALL_DONE
            next.ongoing -> TodayStatus.ONGOING
            else -> TodayStatus.UPCOMING
        }
    }

    /**
     * 计算某条日程在指定日期的**提醒触发时刻**（epoch millis）。
     * 阶段 4 的 AlarmManager / WorkManager 调度直接使用本函数结果，调度逻辑可单测。
     *
     * @param minutesBefore 提前分钟数（用户可自定义）
     */
    fun triggerEpochMillis(
        item: CourseItem,
        date: LocalDate,
        minutesBefore: Int,
        zone: ZoneId = ZoneId.systemDefault()
    ): Long {
        val start = LocalDateTime.of(
            date,
            LocalTime.of(item.startTime / 60, item.startTime % 60)
        ).minusMinutes(minutesBefore.toLong())
        return start.atZone(zone).toInstant().toEpochMilli()
    }

    /**
     * 找出从 [fromDate] 起、在接下来 [lookaheadDays] 天内所有需要排提醒的
     * (日程, 触发时刻) 对。纯函数，不触碰 Android API。
     */
    fun upcomingTriggers(
        items: List<CourseItem>,
        fromDate: LocalDate,
        weekOfDate: (LocalDate) -> Int,
        minutesBefore: Int,
        lookaheadDays: Int = 14,
        zone: ZoneId = ZoneId.systemDefault()
    ): List<Pair<CourseItem, Long>> {
        val result = mutableListOf<Pair<CourseItem, Long>>()
        for (offset in 0 until lookaheadDays) {
            val date = fromDate.plusDays(offset.toLong())
            val week = weekOfDate(date)
            val dayOfWeek = date.dayOfWeek.value
            for (item in items) {
                if (item.dayOfWeek != dayOfWeek) continue
                if (!item.isEffectiveOn(week)) continue
                result += item to triggerEpochMillis(item, date, minutesBefore, zone)
            }
        }
        return result.sortedBy { it.second }
    }
}
