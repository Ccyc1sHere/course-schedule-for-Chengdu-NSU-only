package com.campus.schedule.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * `course_items` 表：每条日程一行。
 *
 * 同一门课在不同周次范围可以是多条独立记录（不强制合并），满足「每周独立排课」。
 */
@Entity(tableName = "course_items")
data class CourseItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "type_key") val typeKey: String,
    @ColumnInfo(name = "day_of_week") val dayOfWeek: Int,
    @ColumnInfo(name = "start_time") val startTime: Int,
    @ColumnInfo(name = "end_time") val endTime: Int,
    @ColumnInfo(name = "location", defaultValue = "''") val location: String = "",
    @ColumnInfo(name = "teacher", defaultValue = "''") val teacher: String = "",
    @ColumnInfo(name = "note", defaultValue = "''") val note: String = "",
    @ColumnInfo(name = "week_start", defaultValue = "1") val weekStart: Int = 1,
    @ColumnInfo(name = "week_end", defaultValue = "18") val weekEnd: Int = 18,
    @ColumnInfo(name = "week_parity", defaultValue = "'ALL'") val weekParity: String = "ALL",
    /** null = 跟随 TypeStyle 的默认色；非 null = 用户在编辑页覆盖的颜色。 */
    @ColumnInfo(name = "color_argb") val colorArgb: Long? = null,
    @ColumnInfo(name = "sort_order", defaultValue = "0") val sortOrder: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
