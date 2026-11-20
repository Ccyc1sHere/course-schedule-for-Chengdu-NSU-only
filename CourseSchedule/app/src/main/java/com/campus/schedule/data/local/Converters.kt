package com.campus.schedule.data.local

import androidx.room.TypeConverter

/** Room 类型转换器。枚举一律以 String 持久化，新增枚举值无需数据库迁移。 */
class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String = value.orEmpty().joinToString("\u001F")

    @TypeConverter
    fun toStringList(value: String?): List<String> =
        if (value.isNullOrEmpty()) emptyList() else value.split("\u001F")
}
