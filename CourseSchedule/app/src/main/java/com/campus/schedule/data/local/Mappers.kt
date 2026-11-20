package com.campus.schedule.data.local

import com.campus.schedule.data.local.entity.AppSettingsEntity
import com.campus.schedule.data.local.entity.CourseItemEntity
import com.campus.schedule.data.local.entity.PeriodTemplateEntity
import com.campus.schedule.data.local.entity.SemesterConfigEntity
import com.campus.schedule.data.local.entity.TypeStyleEntity
import com.campus.schedule.domain.model.AppSettings
import com.campus.schedule.domain.model.CourseItem
import com.campus.schedule.domain.model.PeriodTemplate
import com.campus.schedule.domain.model.SemesterConfig
import com.campus.schedule.domain.model.ThemeMode
import com.campus.schedule.domain.model.TypeStyle
import com.campus.schedule.domain.model.ViewMode
import com.campus.schedule.domain.model.WeekParity
import com.campus.schedule.domain.model.InactiveWeekPolicy
import java.time.LocalDate

/** Entity ↔ Domain 映射，集中在此便于维护单一数据源边界。 */

fun CourseItemEntity.toDomain(): CourseItem = CourseItem(
    id = id,
    title = title,
    typeKey = typeKey,
    dayOfWeek = dayOfWeek,
    startTime = startTime,
    endTime = endTime,
    location = location,
    teacher = teacher,
    note = note,
    weekStart = weekStart,
    weekEnd = weekEnd,
    weekParity = WeekParity.fromKey(weekParity),
    colorArgb = colorArgb,
    sortOrder = sortOrder,
    createdAt = createdAt
)

fun CourseItem.toEntity(): CourseItemEntity = CourseItemEntity(
    id = id,
    title = title,
    typeKey = typeKey,
    dayOfWeek = dayOfWeek,
    startTime = startTime,
    endTime = endTime,
    location = location,
    teacher = teacher,
    note = note,
    weekStart = weekStart,
    weekEnd = weekEnd,
    weekParity = weekParity.key,
    colorArgb = colorArgb,
    sortOrder = sortOrder,
    createdAt = createdAt
)

fun SemesterConfigEntity.toDomain(): SemesterConfig = SemesterConfig(
    startDate = LocalDate.ofEpochDay(startDateEpochDay),
    totalWeeks = totalWeeks
)

fun SemesterConfig.toEntity(): SemesterConfigEntity = SemesterConfigEntity(
    startDateEpochDay = startDate.toEpochDay(),
    totalWeeks = totalWeeks
)

fun PeriodTemplateEntity.toDomain(): PeriodTemplate =
    PeriodTemplate(index = index, label = label, startTime = startTime, endTime = endTime)

fun PeriodTemplate.toEntity(): PeriodTemplateEntity =
    PeriodTemplateEntity(index = index, label = label, startTime = startTime, endTime = endTime)

fun TypeStyleEntity.toDomain(): TypeStyle = TypeStyle(
    key = key,
    displayName = displayName,
    iconKey = iconKey,
    colorArgb = colorArgb,
    sortOrder = sortOrder
)

fun TypeStyle.toEntity(): TypeStyleEntity = TypeStyleEntity(
    key = key,
    displayName = displayName,
    iconKey = iconKey,
    colorArgb = colorArgb,
    sortOrder = sortOrder
)

fun AppSettingsEntity.toDomain(): AppSettings = AppSettings(
    themeMode = runCatching { ThemeMode.valueOf(themeMode) }.getOrDefault(ThemeMode.SYSTEM),
    language = runCatching {
        com.campus.schedule.domain.model.AppLanguage.fromTag(languageTag)
    }.getOrDefault(com.campus.schedule.domain.model.AppLanguage.FOLLOW_SYSTEM),
    dynamicColorEnabled = dynamicColorEnabled,
    defaultView = runCatching { ViewMode.valueOf(defaultView) }.getOrDefault(ViewMode.WEEK),
    inactiveWeekPolicy = runCatching { InactiveWeekPolicy.valueOf(inactiveWeekPolicy) }
        .getOrDefault(InactiveWeekPolicy.DIM),
    showCountdown = showCountdown,
    reminderEnabled = reminderEnabled,
    reminderMinutesBefore = reminderMinutesBefore,
    dndEnabled = dndEnabled,
    partnerOverlayEnabled = partnerOverlayEnabled,
    seedImported = seedImported,
    notificationDeniedOnce = notificationDeniedOnce
)

fun AppSettings.toEntity(): AppSettingsEntity = AppSettingsEntity(
    themeMode = themeMode.name,
    languageTag = language.tag,
    dynamicColorEnabled = dynamicColorEnabled,
    defaultView = defaultView.name,
    inactiveWeekPolicy = inactiveWeekPolicy.name,
    showCountdown = showCountdown,
    reminderEnabled = reminderEnabled,
    reminderMinutesBefore = reminderMinutesBefore,
    dndEnabled = dndEnabled,
    partnerOverlayEnabled = partnerOverlayEnabled,
    seedImported = seedImported,
    notificationDeniedOnce = notificationDeniedOnce
)
