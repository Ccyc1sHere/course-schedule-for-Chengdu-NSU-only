package com.campus.schedule

import com.campus.schedule.domain.logic.ReminderCalculator
import com.campus.schedule.domain.model.CourseItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/** 倒计时与提醒调度纯函数测试。 */
class ReminderCalculatorTest {

    private fun item(id: Long, start: Int, end: Int) =
        CourseItem(id = id, title = "c$id", dayOfWeek = 1, startTime = start, endTime = end)

    @Test
    fun `nextUp returns ongoing course`() {
        val items = listOf(item(1, 600, 690))
        val next = ReminderCalculator.nextUp(items, 620)
        assertNotNull(next)
        assertEquals(1L, next?.item?.id)
        assertEquals(true, next?.ongoing)
        assertEquals(70, next?.minutesUntil)
    }

    @Test
    fun `nextUp returns upcoming course after ongoing ends`() {
        val items = listOf(item(1, 600, 690), item(2, 840, 930))
        val next = ReminderCalculator.nextUp(items, 700)
        assertEquals(2L, next?.item?.id)
        assertEquals(false, next?.ongoing)
        assertEquals(140, next?.minutesUntil)
    }

    @Test
    fun `nextUp returns null when all done`() {
        val items = listOf(item(1, 600, 690))
        assertNull(ReminderCalculator.nextUp(items, 700))
    }

    @Test
    fun `todayStatus reports no course for empty list`() {
        val status = ReminderCalculator.todayStatus(emptyList(), 600, true)
        assertEquals(ReminderCalculator.TodayStatus.NO_COURSE, status)
    }
}
