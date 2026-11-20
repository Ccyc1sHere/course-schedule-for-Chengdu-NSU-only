package com.campus.schedule.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.campus.schedule.R
import com.campus.schedule.domain.model.CourseItem
import com.campus.schedule.ui.components.TimeField

/**
 * 起止时间行：两个 TimeField，点击弹出 M3 TimePicker。
 */
@Composable
fun TimeRow(
    state: EditorUiState,
    viewModel: EditorViewModel,
    onPickStart: () -> Unit,
    onPickEnd: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        TimeField(
            label = stringResource(R.string.field_start_time),
            minutes = state.startTime,
            onClick = onPickStart,
            modifier = Modifier.weight(1f)
        )
        TimeField(
            label = stringResource(R.string.field_end_time),
            minutes = state.endTime,
            onClick = onPickEnd,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * M3 TimePicker，包裹在 AlertDialog 中（跟随 MaterialTheme 配色，深色下无系统亮色弹层）。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogM3(
    initialMinutes: Int,
    onConfirm: (Int) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialMinutes / 60,
        initialMinute = initialMinutes % 60,
        is24Hour = true
    )
    AlertDialog(
        onDismissRequest = { onConfirm(initialMinutes) },
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(stringResource(R.string.field_start_time)) },
        text = {
            TimePicker(state = state)
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(state.hour * 60 + state.minute)
            }) { Text(stringResource(R.string.confirm)) }
        },
        dismissButton = {
            TextButton(onClick = { onConfirm(initialMinutes) }) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * 放弃修改确认对话框（自绘 M3，与主题一致，不使用系统默认样式）。
 */
@Composable
fun DiscardChangesDialog(
    onConfirmDiscard: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(stringResource(R.string.discard_changes_title)) },
        text = { Text(stringResource(R.string.discard_changes_message)) },
        confirmButton = {
            TextButton(onClick = onConfirmDiscard) {
                Text(stringResource(R.string.discard))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/** 供对话框标题复用：格式化分钟为 HH:mm。 */
internal fun formatMin(minutes: Int): String = CourseItem.formatMinutes(minutes)
