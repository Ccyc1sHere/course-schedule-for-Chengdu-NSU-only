package com.campus.schedule.domain.model

/**
 * 伙伴课表的忙碌时段（本地数据，不上传服务器）。
 * 由「分享码」或本地导出文件导入，在周视图以半透明叠加层显示。
 *
 * @property dayOfWeek 1..7
 * @property weekStart/weekEnd 生效周次，便于与本人课表做同一周次口径的对比
 */
data class BusySlot(
    val dayOfWeek: Int,
    val startTime: Int,
    val endTime: Int,
    val weekStart: Int = 1,
    val weekEnd: Int = 18,
    val weekParity: WeekParity = WeekParity.ALL
)

/** 导入的伙伴课表整体。 */
data class PartnerSchedule(
    val name: String,
    val slots: List<BusySlot>,
    val importedAt: Long
)
