package com.campus.schedule.domain.model

/**
 * 类型的可编辑外观：显示名、默认图标、默认颜色。
 *
 * 用户可以修改内置 6 类的任意一项，也可以新增自定义类型（[key] 使用 `CUSTOM_xxx`）。
 * 图标仅存 Material Icons 的键名（字符串），由 UI 层映射为 ImageVector，
 * 不引入网络字体或大图资源。
 *
 * @property colorArgb ARGB Long，例如 0xFF2C6BE5
 */
data class TypeStyle(
    val key: String,
    val displayName: String,
    val iconKey: String,
    val colorArgb: Long,
    val sortOrder: Int
)
