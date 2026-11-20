package com.campus.schedule.ui.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.campus.schedule.R
import com.campus.schedule.domain.model.CourseItem
import com.campus.schedule.domain.model.WeekParity

/**
 * 日程详情：底部弹出面板（ModalBottomSheet，M3 默认 spring 弹性过渡）。
 * 提供编辑 / 删除入口；删除前弹自绘确认对话框（非系统默认样式）。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailSheet(
    item: CourseItem,
    typeName: String,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var confirmDelete by remember { mutableStateOf(false) }
    val parity = when (item.weekParity) {
        WeekParity.ODD -> "（单周）"
        WeekParity.EVEN -> "（双周）"
        else -> ""
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(item.title, style = MaterialTheme.typography.titleLarge)
            Text(typeName, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary)
            Text(
                "${item.startTimeText} - ${item.endTimeText}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (item.location.isNotBlank()) {
                Text(item.location, style = MaterialTheme.typography.bodyMedium)
            }
            if (item.teacher.isNotBlank()) {
                Text(item.teacher, style = MaterialTheme.typography.bodyMedium)
            }
            if (item.note.isNotBlank()) {
                Text(item.note, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                "第 ${item.weekStart}-${item.weekEnd} 周$parity",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onEdit, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.action_edit))
                }
                TextButton(onClick = { confirmDelete = true }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.delete_item))
                }
            }
        }
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = { Text(stringResource(R.string.delete_confirm_message)) },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; onDelete() }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
