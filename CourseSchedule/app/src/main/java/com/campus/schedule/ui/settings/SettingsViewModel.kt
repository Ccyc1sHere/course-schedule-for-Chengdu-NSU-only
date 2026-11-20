package com.campus.schedule.ui.settings

import android.app.NotificationManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campus.schedule.data.repository.ScheduleRepository
import com.campus.schedule.domain.model.AppSettings
import com.campus.schedule.domain.model.PeriodTemplate
import com.campus.schedule.domain.model.SemesterConfig
import com.campus.schedule.domain.model.TypeStyle
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

/** 设置页 ViewModel：所有设置项直接读写 Room，改完即生效。 */
class SettingsViewModel(
    private val repository: ScheduleRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> =
        repository.settings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    val semester: StateFlow<SemesterConfig?> =
        repository.semester.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val periods: StateFlow<List<PeriodTemplate>> =
        repository.periods.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val typeStyles: StateFlow<List<TypeStyle>> =
        repository.typeStyles.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ---------- 外观 / 行为 ----------
    fun setThemeMode(mode: com.campus.schedule.domain.model.ThemeMode) =
        updateSettings { it.copy(themeMode = mode) }

    fun setLanguage(tag: String) =
        updateSettings { it.copy(language = com.campus.schedule.domain.model.AppLanguage.fromTag(tag)) }

    fun setDynamicColor(enabled: Boolean) =
        updateSettings { it.copy(dynamicColorEnabled = enabled) }

    fun setDefaultView(view: com.campus.schedule.domain.model.ViewMode) =
        updateSettings { it.copy(defaultView = view) }

    fun setInactiveWeekPolicy(policy: com.campus.schedule.domain.model.InactiveWeekPolicy) =
        updateSettings { it.copy(inactiveWeekPolicy = policy) }

    fun setShowCountdown(enabled: Boolean) =
        updateSettings { it.copy(showCountdown = enabled) }

    fun setReminderEnabled(enabled: Boolean) =
        updateSettings { it.copy(reminderEnabled = enabled) }

    fun setReminderMinutes(minutes: Int) =
        updateSettings { it.copy(reminderMinutesBefore = minutes.coerceIn(5, 60)) }

    fun setDndEnabled(enabled: Boolean) =
        updateSettings { it.copy(dndEnabled = enabled) }

    fun setPartnerOverlay(enabled: Boolean) =
        updateSettings { it.copy(partnerOverlayEnabled = enabled) }

    fun setNotificationDeniedOnce() =
        updateSettings { it.copy(notificationDeniedOnce = true) }

    /** 开启 / 关闭课前提醒时，同步系统闹钟调度。 */
    fun applyReminderSchedule(context: Context, enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                val items = repository.courses.first()
                val semester = repository.semester.first() ?: return@launch
                com.campus.schedule.notification.ReminderScheduler.schedule(
                    context, items, semester, settings.value.reminderMinutesBefore
                )
            } else {
                com.campus.schedule.notification.ReminderScheduler.cancelAll(context)
            }
        }
    }

    private fun updateSettings(block: (AppSettings) -> AppSettings) {
        viewModelScope.launch { repository.upsertSettings(block(settings.value)) }
    }

    // ---------- 学期 ----------
    fun setSemesterStart(date: LocalDate) {
        viewModelScope.launch {
            val current = semester.value ?: SemesterConfig(date, 18)
            repository.upsertSemester(current.copy(startDate = date))
        }
    }

    fun setTotalWeeks(weeks: Int) {
        viewModelScope.launch {
            val current = semester.value ?: SemesterConfig(LocalDate.now(), weeks)
            repository.upsertSemester(current.copy(totalWeeks = weeks.coerceIn(1, 30)))
        }
    }

    // ---------- 节次表（全部可编辑） ----------
    fun upsertPeriod(period: PeriodTemplate) {
        viewModelScope.launch {
            val list = periods.value.filterNot { it.index == period.index } + period
            repository.upsertPeriods(list.sortedBy { it.index })
        }
    }

    fun upsertPeriods(list: List<PeriodTemplate>) {
        viewModelScope.launch { repository.upsertPeriods(list) }
    }

    fun removePeriod(index: Int) {
        viewModelScope.launch {
            repository.upsertPeriods(periods.value.filterNot { it.index == index })
        }
    }

    // ---------- 类型外观（名称 / 图标 / 颜色可改，可新增自定义类型） ----------
    fun upsertTypeStyle(style: TypeStyle) {
        viewModelScope.launch {
            val list = typeStyles.value.filterNot { it.key == style.key } + style
            repository.upsertTypeStyles(list.sortedBy { it.sortOrder })
        }
    }

    fun upsertTypeStyles(list: List<TypeStyle>) {
        viewModelScope.launch { repository.upsertTypeStyles(list) }
    }

    /** 导出我的课表为分享码（一次性读取）。 */
    fun exportShareCode(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val items = repository.courses.first()
            onResult(com.campus.schedule.util.ShareCode.encodeCourses(items))
        }
    }

    // ---------- 示例数据 ----------
    fun clearSeedCourses(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteAllCourses()
            onDone()
        }
    }

    fun restoreSeedCourses(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.restoreSeedCourses()
            onDone()
        }
    }

    // ---------- 伙伴课表 ----------
    fun savePartner(name: String, slotsJson: String) {
        viewModelScope.launch {
            repository.savePartner(
                com.campus.schedule.domain.model.PartnerSchedule(
                    name = name, slots = emptyList(), importedAt = System.currentTimeMillis()
                ),
                slotsJson
            )
        }
    }

    fun clearPartner() {
        viewModelScope.launch { repository.clearPartner() }
    }

    /** 勿扰权限是否已授予（设置页展示入口用）。 */
    fun isDndAccessGranted(context: Context): Boolean {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return nm.isNotificationPolicyAccessGranted
    }
}
