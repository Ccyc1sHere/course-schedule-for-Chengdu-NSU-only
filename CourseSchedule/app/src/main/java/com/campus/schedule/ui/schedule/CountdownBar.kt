package com.campus.schedule.ui.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.campus.schedule.R
import com.campus.schedule.domain.logic.ReminderCalculator

/** 顶部常驻倒计时条：下一节课 / 进行中 / 今日无课 / 今日课程已结束。 */
@Composable
fun CountdownBar(state: ScheduleUiState) {
    val text = when (state.todayStatus) {
        ReminderCalculator.TodayStatus.NO_COURSE -> stringResource(R.string.today_no_course)
        ReminderCalculator.TodayStatus.ALL_DONE -> stringResource(R.string.today_all_done)
        ReminderCalculator.TodayStatus.OUT_OF_SEMESTER ->
            stringResource(R.string.week_out_of_semester)
        else -> {
            val next = state.nextUp ?: return
            if (next.ongoing) {
                stringResource(R.string.countdown_ongoing, next.minutesUntil)
            } else {
                stringResource(R.string.countdown_next, next.minutesUntil)
            }
        }
    }
    val meta = state.nextUp?.let {
        listOf(it.item.startTimeText, it.item.location)
            .filter { p -> p.isNotBlank() }.joinToString(" · ")
    } ?: ""

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (meta.isNotBlank()) {
                    Text(
                        meta,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
