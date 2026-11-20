package com.campus.schedule.ui.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.campus.schedule.R
import com.campus.schedule.domain.logic.ReminderCalculator
import com.campus.schedule.domain.model.CourseItem
import com.campus.schedule.domain.model.InactiveWeekPolicy

/**
 * 日视图：单日时间轴，按开始时间排列；「正在进行」卡片突出显示，
 * 非本周生效的日程降低透明度并带「非本周」角标（隐藏策略可在设置更改）。
 */
@Composable
fun DayView(
    state: ScheduleUiState,
    viewModel: ScheduleViewModel,
    deletingId: Long?,
    onItemClick: (CourseItem) -> Unit,
    onDeleteComplete: (Long) -> Unit
) {
    val week = state.visibleWeek
    Column(Modifier.fillMaxSize()) {
        DateStrip(state, viewModel)
        if (state.dayItems.isEmpty() && state.dimItems.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    stringResource(R.string.empty_schedule_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Column
        }
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(state.dayItems, key = { it.id }) { item ->
                DayCard(state, item, week, deletingId, onItemClick, onDeleteComplete)
            }
            if (state.settings.inactiveWeekPolicy != InactiveWeekPolicy.HIDE) {
                items(state.dimItems, key = { -it.id }) { item ->
                    DimCard(state, item, deletingId, onItemClick, onDeleteComplete)
                }
            }
        }
    }
}

@Composable
private fun DayCard(
    state: ScheduleUiState,
    item: CourseItem,
    week: Int,
    deletingId: Long?,
    onItemClick: (CourseItem) -> Unit,
    onDeleteComplete: (Long) -> Unit
) {
    val ongoing = state.todayStatus == ReminderCalculator.TodayStatus.ONGOING &&
        state.nextUp?.item?.id == item.id
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        AnimatedCourseCard(
            item = item,
            styles = state.typeStyles,
            dimmed = !item.isEffectiveOn(week),
            highlight = ongoing,
            deleting = deletingId == item.id,
            onClick = { onItemClick(item) },
            onDeleteComplete = { onDeleteComplete(item.id) },
            modifier = Modifier.fillMaxWidth()
        )
        if (ongoing && deletingId != item.id) {
            Text(
                stringResource(R.string.countdown_ongoing, state.nextUp?.minutesUntil ?: 0),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun DimCard(
    state: ScheduleUiState,
    item: CourseItem,
    deletingId: Long?,
    onItemClick: (CourseItem) -> Unit,
    onDeleteComplete: (Long) -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        AnimatedCourseCard(
            item = item,
            styles = state.typeStyles,
            dimmed = true,
            deleting = deletingId == item.id,
            onClick = { onItemClick(item) },
            onDeleteComplete = { onDeleteComplete(item.id) },
            modifier = Modifier.fillMaxWidth()
        )
        if (state.settings.inactiveWeekPolicy == InactiveWeekPolicy.DIM && deletingId != item.id) {
            NotThisWeekBadge(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
            )
        }
    }
}
