package com.campus.schedule.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.campus.schedule.R
import com.campus.schedule.domain.logic.WeekCalculator

/**
 * 日视图顶部日期条：7 天可点选，末尾附「回到本周」快捷按钮。
 */
@Composable
fun DateStrip(state: ScheduleUiState, viewModel: ScheduleViewModel) {
    val semester = state.semester ?: return
    val start = WeekCalculator.mondayOf(semester, state.visibleWeek)
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        (0..6).forEach { i ->
            val date = start.plusDays(i.toLong())
            val selected = date == state.selectedDate
            Box(
                Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(10.dp)
                    )
                    .border(
                        1.dp,
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { viewModel.selectDate(date) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        WeekCalculator.weekdayShort(date),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${date.dayOfMonth}",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Box(
            Modifier
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    RoundedCornerShape(10.dp)
                )
                .clickable { viewModel.backToCurrentWeek() }
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            Text(
                stringResource(R.string.back_to_this_week),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
