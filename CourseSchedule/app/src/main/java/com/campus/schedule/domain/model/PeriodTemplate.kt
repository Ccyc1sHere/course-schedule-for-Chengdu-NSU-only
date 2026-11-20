package com.campus.schedule.domain.model

/**
 * 节次时间表条目。**完全可自定义**：编号、名称、起止时间都能在设置页改，
 * 改后周视图网格与「按节次快速填时间」立即同步。
 *
 * @property index 节次序号（1..N）
 * @property label 显示名，例如「第 1 节」「晚自习」，由用户输入
 * @property startTime 当日分钟数
 * @property endTime 当日分钟数
 */
data class PeriodTemplate(
    val index: Int,
    val label: String,
    val startTime: Int,
    val endTime: Int
)
