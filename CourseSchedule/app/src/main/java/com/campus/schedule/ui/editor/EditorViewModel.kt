package com.campus.schedule.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campus.schedule.data.repository.ScheduleRepository
import com.campus.schedule.domain.model.CourseItem
import com.campus.schedule.domain.model.CourseType
import com.campus.schedule.domain.model.PeriodTemplate
import com.campus.schedule.domain.model.TypeStyle
import com.campus.schedule.domain.model.WeekParity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 编辑页元信息（类型外观 / 节次表 / 总周数），与草稿分离便于独立刷新。
 */
data class EditorMeta(
    val typeStyles: List<TypeStyle> = emptyList(),
    val periods: List<PeriodTemplate> = emptyList(),
    val totalWeeks: Int = 18
)

/**
 * 编辑页草稿。所有字段对用户可见且可编辑。
 *
 * @property colorArgb null 表示「跟随类型默认色」，用户可覆盖
 * @property hasChanges 保存前有修改时，系统返回需弹自绘确认对话框
 */
data class EditorUiState(
    val itemId: Long = 0L,
    val title: String = "",
    val typeKey: String = CourseType.COURSE.key,
    val dayOfWeek: Int = 1,
    val startTime: Int = 600,
    val endTime: Int = 690,
    val location: String = "",
    val teacher: String = "",
    val note: String = "",
    val weekStart: Int = 1,
    val weekEnd: Int = 18,
    val weekParity: WeekParity = WeekParity.ALL,
    val colorArgb: Long? = null,
    val isNew: Boolean = true,
    val saved: Boolean = false,
    val timeInvalid: Boolean = false
) {
    var hasChanges: Boolean = false
        private set

    /** UI 每次 setter 后由 ViewModel 调用，标记草稿已修改。 */
    fun markChanged() {
        hasChanges = true
    }
}

/** 编辑页 ViewModel：草稿保存在内存，点保存才写入 Room。 */
class EditorViewModel(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val draft = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = draft

    /** 类型外观 / 节次表 / 总周数，全部来自 Room（同样可自定义）。 */
    val meta: StateFlow<EditorMeta> = combine(
        repository.typeStyles, repository.periods, repository.semester
    ) { styles, periods, semester ->
        EditorMeta(styles, periods, semester?.totalWeeks ?: 18)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EditorMeta())

    /** 进入编辑页：itemId 为空表示新增（day 为预选星期几）。 */
    fun load(itemId: Long?, day: Int) {
        viewModelScope.launch {
            if (itemId == null || itemId <= 0L) {
                draft.value = EditorUiState(dayOfWeek = day.coerceIn(1, 7), isNew = true)
            } else {
                val course = repository.getCourse(itemId)
                draft.value = if (course == null) {
                    EditorUiState(dayOfWeek = day.coerceIn(1, 7), isNew = true)
                } else {
                    course.toDraft()
                }
            }
        }
    }

    private fun CourseItem.toDraft() = EditorUiState(
        itemId = id,
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
        weekParity = weekParity,
        colorArgb = colorArgb,
        isNew = false
    )

    // ---------- 字段更新（全部先写草稿，保存前不落库） ----------

    private fun update(block: (EditorUiState) -> EditorUiState) {
        draft.value = block(draft.value).apply { markChanged() }
    }

    fun setTitle(v: String) = update { it.copy(title = v) }
    fun setTypeKey(v: String) = update { it.copy(typeKey = v) }
    fun setDayOfWeek(v: Int) = update { it.copy(dayOfWeek = v.coerceIn(1, 7)) }
    fun setLocation(v: String) = update { it.copy(location = v) }
    fun setTeacher(v: String) = update { it.copy(teacher = v) }
    fun setNote(v: String) = update { it.copy(note = v) }
    fun setWeekParity(v: WeekParity) = update { it.copy(weekParity = v) }
    fun setColorArgb(v: Long?) = update { it.copy(colorArgb = v) }

    fun setWeekStart(v: Int) {
        val total = meta.value.totalWeeks
        update {
            it.copy(
                weekStart = v.coerceIn(1, total),
                weekEnd = it.weekEnd.coerceAtLeast(v.coerceIn(1, total))
            )
        }
    }

    fun setWeekEnd(v: Int) {
        val total = meta.value.totalWeeks
        update { it.copy(weekEnd = v.coerceIn(it.weekStart, total)) }
    }

    fun setStartTime(v: Int) = update { it.copy(startTime = v, timeInvalid = v >= it.endTime) }
    fun setEndTime(v: Int) = update { it.copy(endTime = v, timeInvalid = v <= it.startTime) }

    /** 按节次快速填充起止时间（节次表可在设置页自定义）。 */
    fun applyPeriod(period: PeriodTemplate) = update {
        it.copy(startTime = period.startTime, endTime = period.endTime, timeInvalid = false)
    }

    /** 校验：标题非空且结束时间晚于开始时间，纯逻辑。 */
    fun validate(state: EditorUiState = draft.value): Boolean =
        state.title.isNotBlank() && !state.timeInvalid

    /** 保存草稿到 Room。 */
    fun save(onSaved: () -> Unit) {
        val state = draft.value
        if (!validate(state)) return
        viewModelScope.launch {
            val entity = CourseItem(
                id = if (state.isNew) 0L else state.itemId,
                title = state.title.trim(),
                typeKey = state.typeKey,
                dayOfWeek = state.dayOfWeek,
                startTime = state.startTime,
                endTime = state.endTime,
                location = state.location.trim(),
                teacher = state.teacher.trim(),
                note = state.note.trim(),
                weekStart = state.weekStart,
                weekEnd = state.weekEnd,
                weekParity = state.weekParity,
                colorArgb = state.colorArgb,
                sortOrder = 0,
                createdAt = System.currentTimeMillis()
            )
            repository.upsertCourse(entity)
            draft.value = draft.value.copy(saved = true)
            onSaved()
        }
    }
}
