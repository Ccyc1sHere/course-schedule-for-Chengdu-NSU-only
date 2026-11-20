package com.campus.schedule.domain.model

/** 单双周设置。ALL 表示不限制。 */
enum class WeekParity(val key: String) {
    ALL("ALL"),
    ODD("ODD"),
    EVEN("EVEN");

    companion object {
        fun fromKey(key: String): WeekParity =
            entries.firstOrNull { it.key == key } ?: ALL
    }
}
