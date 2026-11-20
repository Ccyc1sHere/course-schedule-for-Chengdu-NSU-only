package com.campus.schedule.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.campus.schedule.R
import com.campus.schedule.domain.model.CourseItem
import com.campus.schedule.domain.model.TypeStyle

/**
 * 类型展示辅助：显示名（内置类型回退到三语资源，用户自定义名优先）、图标、颜色。
 * 全部使用本地 Material Icons，无网络资源。
 */

/** 图标键 -> ImageVector 映射。 */
fun iconForKey(key: String): ImageVector = when (key) {
    "BOOK" -> Icons.AutoMirrored.Filled.MenuBook
    "GROUP" -> Icons.Filled.Groups
    "FITNESS" -> Icons.Filled.FitnessCenter
    "RESTAURANT" -> Icons.Filled.RestaurantMenu
    "CHECK" -> Icons.Filled.FactCheck
    else -> Icons.Filled.Label
}

/** 类型显示名。displayName 为空（内置 6 类默认态）时回退到三语资源。 */
@Composable
fun TypeStyle.displayLabel(): String =
    displayName.ifBlank {
        stringResource(
            when (key) {
                "COURSE" -> R.string.type_course
                "MEETING" -> R.string.type_meeting
                "SPORT" -> R.string.type_sport
                "MEAL" -> R.string.type_meal
                "ATTENDANCE" -> R.string.type_attendance
                else -> R.string.type_other
            }
        )
    }

/** 日程最终颜色：用户覆盖色优先，否则用类型默认色。 */
fun resolveColor(item: CourseItem, styles: List<TypeStyle>): Color {
    val argb = item.colorArgb
        ?: styles.firstOrNull { it.key == item.typeKey }?.colorArgb
        ?: 0xFF6B7280
    return Color(argb)
}
