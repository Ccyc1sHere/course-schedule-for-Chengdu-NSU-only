package com.campus.schedule.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * 品牌配色（Android 10–11 上的一致静态配色）。
 * Android 12+ 若用户开启动态取色则由系统 palette 派生，其余场景始终回落到这里，
 * 保证低版本不出现样式降级。
 */
object BrandColors {
    val Primary = Color(0xFF2C6BE5)
    val PrimaryDark = Color(0xFF9DBCFF)
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Color(0xFFD8E2FF)
    val OnPrimaryContainer = Color(0xFF001A41)
    val PrimaryContainerDark = Color(0xFF1747B0)
    val OnPrimaryContainerDark = Color(0xFFD8E2FF)

    val Secondary = Color(0xFF575E71)
    val SecondaryDark = Color(0xFFBFC6DC)
    val SecondaryContainer = Color(0xFFDBE2F9)
    val OnSecondaryContainer = Color(0xFF141B2C)
    val SecondaryContainerDark = Color(0xFF3F4759)
    val OnSecondaryContainerDark = Color(0xFFDBE2F9)

    val Tertiary = Color(0xFF2FA96B)
    val TertiaryDark = Color(0xFF7EDBA6)
    val TertiaryContainer = Color(0xFFBDF2CC)
    val OnTertiaryContainer = Color(0xFF00210E)
    val TertiaryContainerDark = Color(0xFF005333)
    val OnTertiaryContainerDark = Color(0xFFBDF2CC)

    val Error = Color(0xFFBA1A1A)
    val ErrorDark = Color(0xFFFFB4AB)
    val ErrorContainer = Color(0xFFFFDAD6)
    val OnErrorContainer = Color(0xFF410002)
    val ErrorContainerDark = Color(0xFF93000A)
    val OnErrorContainerDark = Color(0xFFFFDAD6)

    val BackgroundLight = Color(0xFFF7F9FC)
    val OnBackgroundLight = Color(0xFF191C20)
    val BackgroundDark = Color(0xFF10141C)
    val OnBackgroundDark = Color(0xFFE1E2E8)

    val SurfaceLight = Color(0xFFFFFFFF)
    val OnSurfaceLight = Color(0xFF191C20)
    val SurfaceDark = Color(0xFF1A1E27)
    val OnSurfaceDark = Color(0xFFE1E2E8)

    val SurfaceVariantLight = Color(0xFFE0E2EC)
    val OnSurfaceVariantLight = Color(0xFF44474E)
    val SurfaceVariantDark = Color(0xFF2A2F3A)
    val OnSurfaceVariantDark = Color(0xFFC4C6D0)

    val OutlineLight = Color(0xFF74777F)
    val OutlineDark = Color(0xFF8E9099)
    val OutlineVariantLight = Color(0xFFDADCE5)
    val OutlineVariantDark = Color(0xFF44474E)
}
