package com.campus.schedule.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** 节次时间表，行内容全部可编辑（含名称与起止时间）。 */
@Entity(tableName = "period_templates")
data class PeriodTemplateEntity(
    @PrimaryKey val index: Int,
    val label: String,
    val startTime: Int,
    val endTime: Int
)
