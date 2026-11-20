package com.campus.schedule.domain.model

import java.time.LocalDate

/**
 * 学期配置。仅存一条记录，用户可在设置页修改。
 *
 * @property startDate 学期第 1 周周一（示例数据默认 2026-08-31，可在 App 内改）
 * @property totalWeeks 总周数，默认 18
 */
data class SemesterConfig(
    val startDate: LocalDate,
    val totalWeeks: Int = 18
)
