package com.campus.schedule.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** 应用设置，固定单行 id = 1。所有字段均可在设置页修改。 */
@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val themeMode: String = "SYSTEM",
    val languageTag: String = "",
    val dynamicColorEnabled: Boolean = true,
    val defaultView: String = "WEEK",
    val inactiveWeekPolicy: String = "DIM",
    val showCountdown: Boolean = true,
    val reminderEnabled: Boolean = false,
    val reminderMinutesBefore: Int = 15,
    val dndEnabled: Boolean = false,
    val partnerOverlayEnabled: Boolean = false,
    val seedImported: Boolean = false,
    val notificationDeniedOnce: Boolean = false
)
