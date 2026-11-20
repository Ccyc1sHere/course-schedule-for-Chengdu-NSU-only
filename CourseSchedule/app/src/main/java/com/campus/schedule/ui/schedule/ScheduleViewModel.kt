package com.campus.schedule.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campus.schedule.data.repository.ScheduleRepository
import com.campus.schedule.data.repository.ScheduleSnapshot
import com.campus.schedule.domain.logic.ReminderCalculator
import com.campus.schedule.domain.logic.WeekCalculator
import com.campus.schedule.domain.model.AppSettings
import com.campus.schedule.domain.model.CourseItem
import com.campus.schedule.domain.model.PeriodTemplate
import com.campus.schedule.domain.model.SemesterConfig
import com.campus.schedule.domain.model.TypeStyle
import com.campus.schedule.domain.model.ViewMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

/** 当前「当日分钟数」，供默认参数与 ViewModel 共用。 */
private fun currentMinuteOfNow(): Int = LocalTime.now().let { it.hour * 60 + it.minute }

/**
 * 主界面状态。全部来源于 Repository 的 Flow（单一数据源），派生字段在 combine 中计算。
 *
 * @property weekItems 当周生效日程（周视图主体）
 * @property dayItems 选中日当周生效日程
 * @property dimItems 选中日「非本周生效」的日程（降级显示：低透明度 + 「非本周」角标）
 */
data class ScheduleUiState(
    val loading: Boolean = true,
    val semester: SemesterConfig? = null,
    val periods: List<PeriodTemplate> = emptyList(),
    val typeStyles: List<TypeStyle> = emptyList(),
    val settings: AppSettings = AppSettings(),
    val today: LocalDate = LocalDate.now(),
    val nowMinute: Int = currentMinuteOfNow(),
    val currentWeek: Int = 1,
    val viewMode: ViewMode = ViewMode.WEEK,
    val selectedDate: LocalDate = LocalDate.now(),
    val courses: List<CourseItem> = emptyList(),
    val weekItems: List<CourseItem> = emptyList(),
    val dayItems: List<CourseItem> = emptyList(),
    val dimItems: List<CourseItem> = emptyList(),
    val nextUp: ReminderCalculator.NextUp? = null,
    val todayStatus: ReminderCalculator.TodayStatus =
        ReminderCalculator.TodayStatus.NO_COURSE
) {
    /** 当前定位日期所在周次（出学期范围时可能小于 1 或大于总周数）。 */
    val visibleWeek: Int
        get() = semester?.let { WeekCalculator.weekOf(it, selectedDate) } ?: 1
}

/** 主界面 ViewModel。 */
class ScheduleViewModel(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val viewMode = MutableStateFlow(ViewMode.WEEK)
    private val selectedDate = MutableStateFlow(LocalDate.now())
    private val nowMinute = MutableStateFlow(currentMinuteOfNow())

    init {
        // 每 30 秒刷新一次倒计时（不是每帧），避免无谓重组
        viewModelScope.launch {
            while (true) {
                delay(30_000)
                nowMinute.value = currentMinuteOfNow()
            }
        }
    }

    val uiState: StateFlow<ScheduleUiState> = combine(
        repository.snapshot, viewMode, selectedDate, nowMinute
    ) { snap, mode, date, now ->
        buildState(snap, mode, date, now)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ScheduleUiState())

    private fun buildState(
        snap: ScheduleSnapshot,
        mode: ViewMode,
        date: LocalDate,
        now: Int
    ): ScheduleUiState {
        val semester = snap.semester
        val today = LocalDate.now()
        val currentWeek = semester?.let { WeekCalculator.currentWeek(it, today) } ?: 1
        val visibleWeek = semester?.let { WeekCalculator.weekOf(it, date) } ?: 1
        val weekItems = snap.courses.filter { it.isEffectiveOn(visibleWeek) }
        val dayOfWeek = date.dayOfWeek.value
        val dayItems = snap.courses.filter {
            it.dayOfWeek == dayOfWeek && it.isEffectiveOn(visibleWeek)
        }
        val dimItems = snap.courses.filter {
            it.dayOfWeek == dayOfWeek && !it.isEffectiveOn(visibleWeek)
        }
        // 顶部倒计时与「正在进行」永远基于「今天」，与浏览中的 selectedDate 解耦
        val todayItems = snap.courses.filter {
            it.dayOfWeek == today.dayOfWeek.value && it.isEffectiveOn(currentWeek)
        }
        val nextUp = ReminderCalculator.nextUp(todayItems, now)
        val status = ReminderCalculator.todayStatus(
            todayItems, now,
            WeekCalculator.isWithinSemester(semester ?: return emptyState(mode, date), currentWeek)
        )
        return ScheduleUiState(
            loading = false,
            semester = semester,
            periods = snap.periods,
            typeStyles = snap.typeStyles,
            settings = snap.settings,
            today = today,
            nowMinute = now,
            currentWeek = currentWeek,
            viewMode = mode,
            selectedDate = date,
            courses = snap.courses,
            weekItems = weekItems,
            dayItems = dayItems,
            dimItems = dimItems,
            nextUp = nextUp,
            todayStatus = status
        )
    }

    private fun emptyState(mode: ViewMode, date: LocalDate) = ScheduleUiState(
        loading = true, viewMode = mode, selectedDate = date
    )

    // ---------- 用户操作 ----------

    fun setViewMode(mode: ViewMode) {
        viewMode.value = mode
    }

    /** 日视图 / 周视图共享同一份定位日期，切换时保持定位不变。 */
    fun selectDate(date: LocalDate) {
        selectedDate.value = date
    }

    /** 周视图左右滑动切周：日期整体平移一周，周次与网格随之变化。 */
    fun shiftWeek(offset: Int) {
        selectedDate.value = selectedDate.value.plusWeeks(offset.toLong())
    }

    /** 「回到本周」快捷按钮。 */
    fun backToCurrentWeek() {
        selectedDate.value = LocalDate.now()
    }

    /** 删除日程；UI 层先播放收缩 + 淡出动画，动画结束后才调用此提交删除。 */
    fun deleteItem(id: Long) {
        viewModelScope.launch { repository.deleteCourse(id) }
    }

    /** 整周复制：把某一周生效的全部日程复制到另一周，返回复制条数供提示。 */
    fun copyWeek(fromWeek: Int, toWeek: Int, onDone: (Int) -> Unit) {
        viewModelScope.launch {
            val count = repository.copyWeek(fromWeek, toWeek)
            onDone(count)
        }
    }
}
