package com.campus.schedule.domain.model

import com.campus.schedule.domain.logic.WeekCalculator

/**
 * 一条日程的领域模型（唯一数据源的对外形态）。
 *
 * 设计要点：
 * - [startTime] / [endTime] 使用「当日第几分钟」（0..1440），排序、冲突检测、倒计时均为纯数值运算。
 * - [teacher] 与 [note] 为两个独立字段；看不清 / 不清楚的信息一律留空，由用户自行填写。
 * - [colorArgb] 为 null 时表示「跟随类型默认色」，用户可在编辑页覆盖；类型默认色本身也可在设置页改。
 * - [sortOrder] 仅用于同一天内拖动排序的展示顺序，不影响时间轴定位。
 * - 同一门课允许在不同 [weekStart]/[weekEnd] 创建多条独立记录（每周独立排课场景）。
 */
data class CourseItem(
    val id: Long = 0L,
    val title: String,
    val typeKey: String = CourseType.COURSE.key,
    val dayOfWeek: Int,
    val startTime: Int,
    val endTime: Int,
    val location: String = "",
    val teacher: String = "",
    val note: String = "",
    val weekStart: Int = 1,
    val weekEnd: Int = 18,
    val weekParity: WeekParity = WeekParity.ALL,
    val colorArgb: Long? = null,
    val sortOrder: Int = 0,
    val createdAt: Long = 0L
) {
    /** 该日程在指定周次是否生效（周次范围 + 单双周）。 */
    fun isEffectiveOn(week: Int): Boolean =
        WeekCalculator.isEffective(weekStart, weekEnd, weekParity, week)

    /** 开始时间格式化，例如 620 -> "10:20"。 */
    val startTimeText: String get() = formatMinutes(startTime)

    /** 结束时间格式化。 */
    val endTimeText: String get() = formatMinutes(endTime)

    companion object {
        /** 分钟数转 HH:mm，纯函数。 */
        fun formatMinutes(minutes: Int): String {
            val h = minutes / 60
            val m = minutes % 60
            return "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"
        }

        /** HH:mm 转分钟数，解析失败返回 null，纯函数。 */
        fun parseMinutes(text: String): Int? {
            val parts = text.split(":")
            if (parts.size != 2) return null
            val h = parts[0].toIntOrNull() ?: return null
            val m = parts[1].toIntOrNull() ?: return null
            if (h !in 0..23 || m !in 0..59) return null
            return h * 60 + m
        }
    }
}
