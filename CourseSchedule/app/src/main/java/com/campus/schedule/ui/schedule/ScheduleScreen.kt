package com.campus.schedule.ui.schedule

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.campus.schedule.R
import com.campus.schedule.domain.model.CourseItem
import com.campus.schedule.domain.model.ViewMode
import com.campus.schedule.ui.components.displayLabel
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * 主界面：标题 + 倒计时条 + 日/周切换（Shared Axis 风格转场）+ 新增/复制/设置入口。
 * 统一管理详情面板与删除动画（先收缩淡出，再提交数据库删除）。
 */
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    onEditItem: (Long?, Int) -> Unit,
    onOpenSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var copyDialog by remember { mutableStateOf(false) }
    var detailItem by remember { mutableStateOf<CourseItem?>(null) }
    var deletingId by remember { mutableStateOf<Long?>(null) }
    val weekText = stringResource(R.string.week_label, state.visibleWeek)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onEditItem(null, state.selectedDate.dayOfWeek.value) },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.add_item)) }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = state.selectedDate.format(
                            DateTimeFormatter.ofPattern("M月d日 ", Locale.getDefault())
                        ) + state.selectedDate.dayOfWeek.getDisplayName(
                            TextStyle.FULL, Locale.getDefault()
                        ),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = weekText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { copyDialog = true }) {
                    Icon(
                        Icons.Filled.ContentCopy,
                        contentDescription = stringResource(R.string.copy_day_title)
                    )
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.nav_settings)
                    )
                }
            }

            if (state.settings.showCountdown) CountdownBar(state)

            SingleChoiceSegmentedButtonRow(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SegmentedButton(
                    selected = state.viewMode == ViewMode.WEEK,
                    onClick = { viewModel.setViewMode(ViewMode.WEEK) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) { Text(stringResource(R.string.view_week)) }
                SegmentedButton(
                    selected = state.viewMode == ViewMode.DAY,
                    onClick = { viewModel.setViewMode(ViewMode.DAY) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) { Text(stringResource(R.string.view_day)) }
            }

            AnimatedContent(
                targetState = state.viewMode,
                transitionSpec = {
                    val dir = if (targetState == ViewMode.DAY) 1 else -1
                    (slideInHorizontally { it / 4 * dir } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it / 4 * dir } + fadeOut())
                },
                label = "viewSwitch"
            ) { mode ->
                when (mode) {
                    ViewMode.WEEK -> WeekView(
                        state = state,
                        viewModel = viewModel,
                        deletingId = deletingId,
                        onItemClick = { detailItem = it },
                        onDeleteComplete = { id ->
                            viewModel.deleteItem(id)
                            deletingId = null
                        }
                    )
                    ViewMode.DAY -> DayView(
                        state = state,
                        viewModel = viewModel,
                        deletingId = deletingId,
                        onItemClick = { detailItem = it },
                        onDeleteComplete = { id ->
                            viewModel.deleteItem(id)
                            deletingId = null
                        }
                    )
                }
            }
        }
    }

    if (copyDialog) {
        CopyWeekDialog(
            totalWeeks = state.semester?.totalWeeks ?: 18,
            onDismiss = { copyDialog = false },
            onCopy = { from, to ->
                viewModel.copyWeek(from, to) { copyDialog = false }
            }
        )
    }

    detailItem?.let { item ->
        val style = state.typeStyles.firstOrNull { it.key == item.typeKey }
        ItemDetailSheet(
            item = item,
            typeName = style?.displayLabel() ?: item.typeKey,
            onDismiss = { detailItem = null },
            onEdit = {
                detailItem = null
                onEditItem(item.id, item.dayOfWeek)
            },
            onDelete = {
                detailItem = null
                deletingId = item.id
            }
        )
    }
}
