package com.campus.schedule

import com.campus.schedule.domain.logic.WeekCalculator
import com.campus.schedule.domain.model.SemesterConfig
import com.campus.schedule.domain.model.WeekParity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/** 周次引擎纯函数测试。 */
class WeekCalculatorTest {

    private val semester = SemesterConfig(LocalDate.of(2026, 8, 31), 18)

    @Test
    fun `weekOf returns 1 for semester start`() {
        assertEquals(1, WeekCalculator.weekOf(semester, LocalDate.of(2026, 8, 31)))
    }

    @Test
    fun `weekOf increments by week`() {
        assertEquals(2, WeekCalculator.weekOf(semester, LocalDate.of(2026, 9, 7)))
        assertEquals(18, WeekCalculator.weekOf(semester, LocalDate.of(2026, 12, 28)))
    }

    @Test
    fun `alignToMonday rounds down to monday`() {
        val wed = LocalDate.of(2026, 9, 2) // 周三
        assertEquals(LocalDate.of(2026, 8, 31), WeekCalculator.alignToMonday(wed))
    }

    @Test
    fun `isEffective respects week range and parity`() {
        assertTrue(WeekCalculator.isEffective(1, 16, WeekParity.ALL, 8))
        assertFalse(WeekCalculator.isEffective(1, 16, WeekParity.ALL, 17))
        assertTrue(WeekCalculator.isEffective(6, 12, WeekParity.ODD, 7))
        assertFalse(WeekCalculator.isEffective(6, 12, WeekParity.ODD, 8))
        assertTrue(WeekCalculator.isEffective(6, 12, WeekParity.EVEN, 8))
    }

    @Test
    fun `datesOfWeek returns seven days starting monday`() {
        val dates = WeekCalculator.datesOfWeek(semester, 1)
        assertEquals(7, dates.size)
        assertEquals(LocalDate.of(2026, 8, 31), dates.first())
        assertEquals(LocalDate.of(2026, 9, 6), dates.last())
    }
}
