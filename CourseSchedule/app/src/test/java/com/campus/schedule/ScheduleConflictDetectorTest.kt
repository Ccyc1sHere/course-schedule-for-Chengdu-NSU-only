package com.campus.schedule

import com.campus.schedule.domain.logic.ScheduleConflictDetector
import com.campus.schedule.domain.model.CourseItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** 冲突检测与并排布局纯函数测试。 */
class ScheduleConflictDetectorTest {

    private fun item(id: Long, day: Int, start: Int, end: Int) =
        CourseItem(id = id, title = "c$id", dayOfWeek = day, startTime = start, endTime = end)

    @Test
    fun `overlaps detects overlapping intervals`() {
        assertTrue(
            ScheduleConflictDetector.overlaps(item(1, 1, 600, 690), item(2, 1, 650, 740))
        )
        assertFalse(
            ScheduleConflictDetector.overlaps(item(1, 1, 600, 650), item(2, 1, 650, 700))
        )
        // 不同天不重叠
        assertFalse(
            ScheduleConflictDetector.overlaps(item(1, 1, 600, 700), item(2, 2, 600, 700))
        )
    }

    @Test
    fun `assignLanes places overlapping items side by side`() {
        val items = listOf(
            item(1, 1, 600, 690),
            item(2, 1, 650, 740),
            item(3, 1, 800, 850)
        )
        val lanes = ScheduleConflictDetector.assignLanes(items)
        // 前两条重叠，第三条独立
        assertEquals(2, lanes[1]?.laneCount)
        assertEquals(2, lanes[2]?.laneCount)
        assertEquals(1, lanes[3]?.laneCount)
        assertEquals(0, lanes[1]?.lane)
        assertEquals(1, lanes[2]?.lane)
    }

    @Test
    fun `findConflicts returns conflict pairs`() {
        val items = listOf(
            item(1, 1, 600, 690),
            item(2, 1, 650, 740),
            item(3, 2, 600, 700)
        )
        val conflicts = ScheduleConflictDetector.findConflicts(items)
        assertEquals(1, conflicts.size)
        assertEquals(1L to 2L, conflicts.first().first.id to conflicts.first().second.id)
    }
}
