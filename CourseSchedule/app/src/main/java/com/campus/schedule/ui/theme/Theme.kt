package com.campus.schedule.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.campus.schedule.domain.model.ThemeMode

/** 应用当前是否处于深色模式，供非 Compose 场景（小组件等）间接同步。 */
val LocalDarkTheme = staticCompositionLocalOf { false }

private val LightScheme = lightColorScheme(
    primary = BrandColors.Primary,
    onPrimary = BrandColors.OnPrimary,
    primaryContainer = BrandColors.PrimaryContainer,
    onPrimaryContainer = BrandColors.OnPrimaryContainer,
    secondary = BrandColors.Secondary,
    onSecondary = Color.White,
    secondaryContainer = BrandColors.SecondaryContainer,
    onSecondaryContainer = BrandColors.OnSecondaryContainer,
    tertiary = BrandColors.Tertiary,
    onTertiary = Color.White,
    tertiaryContainer = BrandColors.TertiaryContainer,
    onTertiaryContainer = BrandColors.OnTertiaryContainer,
    error = BrandColors.Error,
    onError = Color.White,
    errorContainer = BrandColors.ErrorContainer,
    onErrorContainer = BrandColors.OnErrorContainer,
    background = BrandColors.BackgroundLight,
    onBackground = BrandColors.OnBackgroundLight,
    surface = BrandColors.SurfaceLight,
    onSurface = BrandColors.OnSurfaceLight,
    surfaceVariant = BrandColors.SurfaceVariantLight,
    onSurfaceVariant = BrandColors.OnSurfaceVariantLight,
    outline = BrandColors.OutlineLight,
    outlineVariant = BrandColors.OutlineVariantLight
)

private val DarkScheme = darkColorScheme(
    primary = BrandColors.PrimaryDark,
    onPrimary = Color(0xFF00306B),
    primaryContainer = BrandColors.PrimaryContainerDark,
    onPrimaryContainer = BrandColors.OnPrimaryContainerDark,
    secondary = BrandColors.SecondaryDark,
    onSecondary = Color(0xFF293041),
    secondaryContainer = BrandColors.SecondaryContainerDark,
    onSecondaryContainer = BrandColors.OnSecondaryContainerDark,
    tertiary = BrandColors.TertiaryDark,
    onTertiary = Color(0xFF00391F),
    tertiaryContainer = BrandColors.TertiaryContainerDark,
    onTertiaryContainer = BrandColors.OnTertiaryContainerDark,
    error = BrandColors.ErrorDark,
    onError = Color(0xFF690005),
    errorContainer = BrandColors.ErrorContainerDark,
    onErrorContainer = BrandColors.OnErrorContainerDark,
    background = BrandColors.BackgroundDark,
    onBackground = BrandColors.OnBackgroundDark,
    surface = BrandColors.SurfaceDark,
    onSurface = BrandColors.OnSurfaceDark,
    surfaceVariant = BrandColors.SurfaceVariantDark,
    onSurfaceVariant = BrandColors.OnSurfaceVariantDark,
    outline = BrandColors.OutlineDark,
    outlineVariant = BrandColors.OutlineVariantDark
)

/**
 * 对 [ColorScheme] 的每个颜色做约 300ms 平滑过渡（animateColorAsState），
 * 主题切换（浅 / 深 / 跟随系统）时全局颜色渐变而非生硬跳变。
 */
@Composable
fun animateColorScheme(target: ColorScheme): ColorScheme {
    @Composable
    fun animated(color: Color): Color = animateColorAsState(
        targetValue = color,
        animationSpec = tween(durationMillis = 300),
        label = "themeColorTransition"
    ).value

    return ColorScheme(
        primary = animated(target.primary),
        onPrimary = animated(target.onPrimary),
        primaryContainer = animated(target.primaryContainer),
        onPrimaryContainer = animated(target.onPrimaryContainer),
        inversePrimary = animated(target.inversePrimary),
        secondary = animated(target.secondary),
        onSecondary = animated(target.onSecondary),
        secondaryContainer = animated(target.secondaryContainer),
        onSecondaryContainer = animated(target.onSecondaryContainer),
        tertiary = animated(target.tertiary),
        onTertiary = animated(target.onTertiary),
        tertiaryContainer = animated(target.tertiaryContainer),
        onTertiaryContainer = animated(target.onTertiaryContainer),
        background = animated(target.background),
        onBackground = animated(target.onBackground),
        surface = animated(target.surface),
        onSurface = animated(target.onSurface),
        surfaceVariant = animated(target.surfaceVariant),
        onSurfaceVariant = animated(target.onSurfaceVariant),
        surfaceTint = animated(target.surfaceTint),
        inverseSurface = animated(target.inverseSurface),
        inverseOnSurface = animated(target.inverseOnSurface),
        error = animated(target.error),
        onError = animated(target.onError),
        errorContainer = animated(target.errorContainer),
        onErrorContainer = animated(target.onErrorContainer),
        outline = animated(target.outline),
        outlineVariant = animated(target.outlineVariant),
        scrim = animated(target.scrim)
    )
}

/**
 * 应用主题入口。
 *
 * @param themeMode LIGHT / DARK / SYSTEM，由设置页一键切换
 * @param dynamicColorEnabled Android 12+ 动态取色开关；Android 10–11 恒为品牌配色
 */
@Composable
fun CourseScheduleTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColorEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemDark
    }
    val context = LocalContext.current
    val baseScheme = if (
        dynamicColorEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    ) {
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (darkTheme) DarkScheme else LightScheme
    }
    val scheme = animateColorScheme(baseScheme)

    // Edge-to-Edge：状态栏 / 导航栏图标颜色随主题切换（浅色用深色图标，深色用浅色图标）
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    androidx.compose.runtime.CompositionLocalProvider(LocalDarkTheme provides darkTheme) {
        androidx.compose.material3.MaterialTheme(
            colorScheme = scheme,
            typography = AppTypography,
            content = content
        )
    }
}
