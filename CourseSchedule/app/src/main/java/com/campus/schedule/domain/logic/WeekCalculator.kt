package com.campus.schedule.domain.logic

import com.campus.schedule.domain.model.SemesterConfig
import com.campus.schedule.domain.model.WeekParity
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * 周次引擎：**全部为不依赖 Android 的纯函数**，便于 JVM 单元测试。
 *
 * 约定：学期第 1 周周一为 [SemesterConfig.startDate]；若用户填了别的星期几，
 * 会自动向前对齐到该周周一，避免「第几周」算错一天。
 */
object WeekCalculator {

    /** 把任意日期对齐到所在周的周一（ISO：周一为一周第一天）。 */
    fun alignToMonday(date: LocalDate): LocalDate =
        date.minusDays((date.dayOfWeek.value - 1).toLong())

    /**
     * 计算 [date] 处于学期第几周（1-based）。
     * 返回值可能小于 1（开学前）或大于 totalWeeks（学期结束后），由 UI 决定展示文案。
     */
    fun weekOf(semester: SemesterConfig, date: LocalDate): Int {
        val monday = alignToMonday(semester.startDate)
        val days = ChronoUnit.DAYS.between(monday, alignToMonday(date))
        return (days / 7).toInt() + 1
    }

    /** 当前日期对应的周次。抽成单独函数便于在测试中注入固定「今天」。 */
    fun currentWeek(semester: SemesterConfig, today: LocalDate = LocalDate.now()): Int =
        weekOf(semester, today)

    /** 某一周周一的日期。 */
    fun mondayOf(semester: SemesterConfig, week: Int): LocalDate =
        alignToMonday(semester.startDate).plusWeeks((week - 1).toLong())

    /** 某一周包含的全部 7 天（周一 -> 周日）。 */
    fun datesOfWeek(semester: SemesterConfig, week: Int): List<LocalDate> {
        val monday = mondayOf(semester, week)
        return (0..6).map { monday.plusDays(it.toLong()) }
    }

    /**
     * 判断日程在指定周次是否生效。
     *
     * 单双周以「学期周次」的奇偶为准（不是自然周），与教务系统口径一致。
     * 允许同一门课创建多条不同 [weekStart]/[weekEnd] 的记录，逐条独立判断，不合并。
     */
    fun isEffective(weekStart: Int, weekEnd: Int, parity: WeekParity, week: Int): Boolean {
        if (week !in weekStart..weekEnd) return false
        return when (parity) {
            WeekParity.ALL -> true
            WeekParity.ODD -> week % 2 == 1
            WeekParity.EVEN -> week % 2 == 0
        }
    }

    /** 该周次是否在学期范围内。 */
    fun isWithinSemester(semester: SemesterConfig, week: Int): Boolean =
        week in 1..semester.totalWeeks

    /** 星期几的短名（供日视图日期条显示），如「一 / 二 / 三」。 */
    fun weekdayShort(date: java.time.LocalDate): String =
        when (date.dayOfWeek.value) {
            1 -> "一"; 2 -> "二"; 3 -> "三"; 4 -> "四"
            5 -> "五"; 6 -> "六"; else -> "日"
        }
}
