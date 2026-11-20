package com.campus.schedule.domain.model

/** 主题模式。SYSTEM 跟随系统。 */
enum class ThemeMode { LIGHT, DARK, SYSTEM }

/** 默认进入的视图。 */
enum class ViewMode { DAY, WEEK }

/** 非本周生效日程的显示策略，默认 DIM（降低透明度 + 「非本周」角标）。 */
enum class InactiveWeekPolicy { DIM, HIDE, PLACEHOLDER }

/** 应用内语言，FOLLOW_SYSTEM 时由系统 locale 决定。 */
enum class AppLanguage(val tag: String) {
    FOLLOW_SYSTEM(""),
    ZH_CN("zh-CN"),
    ZH_TW("zh-TW"),
    EN("en");

    companion object {
        fun fromTag(tag: String): AppLanguage =
            entries.firstOrNull { it.tag == tag } ?: FOLLOW_SYSTEM
    }
}

/**
 * 应用设置（Room 中单行持久化），**每一项都可在设置页修改**。
 *
 * @property seedImported 示例课表是否已导入；用户清空后不会再次自动灌入
 * @property reminderEnabled 只有用户主动开启时才申请 POST_NOTIFICATIONS
 * @property notificationDeniedOnce 权限被拒后不再自动弹窗，仅在设置页留入口
 */
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.FOLLOW_SYSTEM,
    val dynamicColorEnabled: Boolean = true,
    val defaultView: ViewMode = ViewMode.WEEK,
    val inactiveWeekPolicy: InactiveWeekPolicy = InactiveWeekPolicy.DIM,
    val showCountdown: Boolean = true,
    val reminderEnabled: Boolean = false,
    val reminderMinutesBefore: Int = 15,
    val dndEnabled: Boolean = false,
    val partnerOverlayEnabled: Boolean = false,
    val seedImported: Boolean = false,
    val notificationDeniedOnce: Boolean = false
)
