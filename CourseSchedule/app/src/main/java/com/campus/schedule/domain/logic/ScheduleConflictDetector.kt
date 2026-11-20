package com.campus.schedule.domain.logic

import com.campus.schedule.domain.model.CourseItem

/**
 * 冲突检测与周视图并排布局算法（纯函数，无 Android 依赖）。
 *
 * 采用「重叠连通分组 + 贪心列分配」：同一天内互相重叠的卡片被分进同一组，
 * 组内按开始时间依次占用第一个空闲列，得到 [LanePlacement]。
 */
object ScheduleConflictDetector {

    /** 单个卡片在重叠组内的布局结果：列序号与总列数，均为 0-based。 */
    data class LanePlacement(val lane: Int, val laneCount: Int)

    /** 两个日程在时间上是否重叠（半开区间，首尾相接不算重叠）。 */
    fun overlaps(a: CourseItem, b: CourseItem): Boolean =
        a.dayOfWeek == b.dayOfWeek && a.startTime < b.endTime && b.startTime < a.endTime

    /** 返回所有冲突对，供编辑页提示 / 测试断言。 */
    fun findConflicts(items: List<CourseItem>): List<Pair<CourseItem, CourseItem>> {
        val byDay = items.groupBy { it.dayOfWeek }
        val result = mutableListOf<Pair<CourseItem, CourseItem>>()
        for (list in byDay.values) {
            val sorted = list.sortedWith(compareBy({ it.startTime }, { it.id }))
            for (i in sorted.indices) {
                for (j in i + 1 until sorted.size) {
                    if (sorted[j].startTime >= sorted[i].endTime) break
                    if (overlaps(sorted[i], sorted[j])) result += sorted[i] to sorted[j]
                }
            }
        }
        return result
    }

    /**
     * 为同一天的日程分配并排列。返回 id -> [LanePlacement]；
     * 不重叠的日程 laneCount 均为 1。
     */
    fun assignLanes(items: List<CourseItem>): Map<Long, LanePlacement> {
        val result = mutableMapOf<Long, LanePlacement>()
        for ((day, dayItems) in items.groupBy { it.dayOfWeek }) {
            val sorted = dayItems.sortedWith(compareBy({ it.startTime }, { it.endTime }, { it.id }))
            var cursor = 0
            while (cursor < sorted.size) {
                // 以「最大结束时间」扩张出一个重叠连通组
                var groupEnd = sorted[cursor].endTime
                var end = cursor + 1
                while (end < sorted.size && sorted[end].startTime < groupEnd) {
                    groupEnd = maxOf(groupEnd, sorted[end].endTime)
                    end++
                }
                val group = sorted.subList(cursor, end)
                val laneEnds = mutableListOf<Int>()
                val placements = mutableMapOf<Long, Int>()
                for (item in group) {
                    var lane = laneEnds.indexOfFirst { it <= item.startTime }
                    if (lane == -1) {
                        lane = laneEnds.size
                        laneEnds.add(item.endTime)
                    } else {
                        laneEnds[lane] = item.endTime
                    }
                    placements[item.id] = lane
                }
                val count = laneEnds.size
                for (item in group) {
                    result[item.id] = LanePlacement(placements[item.id] ?: 0, count)
                }
                cursor = end
            }
        }
        return result
    }
}
