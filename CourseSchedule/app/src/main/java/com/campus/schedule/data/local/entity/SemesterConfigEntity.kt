package com.campus.schedule.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** 学期配置，固定单行 id = 1。 */
@Entity(tableName = "semester_config")
data class SemesterConfigEntity(
    @PrimaryKey val id: Int = 1,
    /** 学期第 1 周周一的 epochDay（LocalDate.toEpochDay()）。 */
    val startDateEpochDay: Long,
    val totalWeeks: Int = 18
)
