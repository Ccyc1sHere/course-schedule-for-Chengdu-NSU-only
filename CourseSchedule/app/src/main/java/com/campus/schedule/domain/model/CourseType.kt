package com.campus.schedule.domain.model

/**
 * 内置日程类型。`key` 为持久化值，与 [TypeStyle] 关联。
 *
 * 注意：类型的显示名称、图标、默认颜色**不写死在这里**，而是存放在 Room 的
 * `type_styles` 表中并由用户编辑；本枚举只提供 6 个内置默认项与顺序。
 */
enum class CourseType(val key: String) {
    COURSE("COURSE"),
    MEETING("MEETING"),
    SPORT("SPORT"),
    MEAL("MEAL"),
    ATTENDANCE("ATTENDANCE"),
    OTHER("OTHER");

    companion object {
        /** 安全解析：未知 key（含用户自定义类型）一律回退到 OTHER，避免崩溃。 */
        fun fromKey(key: String): CourseType =
            entries.firstOrNull { it.key == key } ?: OTHER
    }
}
