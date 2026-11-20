package com.campus.schedule.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** 类型外观表：显示名 / 图标键 / 默认颜色 / 排序，全部可编辑，也允许新增自定义类型。 */
@Entity(tableName = "type_styles")
data class TypeStyleEntity(
    @PrimaryKey val key: String,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "icon_key") val iconKey: String,
    @ColumnInfo(name = "color_argb") val colorArgb: Long,
    @ColumnInfo(name = "sort_order") val sortOrder: Int
)
