package com.campus.schedule.ui.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.campus.schedule.R

/**
 * 整周复制对话框：把某周生效的全部日程复制到另一周（单周生效）。
 * 解决「这周课表和上一周几乎一样」的反复录入痛点。
 */
@Composable
fun CopyWeekDialog(
    totalWeeks: Int,
    onDismiss: () -> Unit,
    onCopy: (Int, Int) -> Unit
) {
    var from by remember { mutableStateOf(1) }
    var to by remember { mutableStateOf(2) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(stringResource(R.string.copy_day_title)) },
        text = {
            Column {
                WeekStepper(stringResource(R.string.copy_day_from), from, totalWeeks) { from = it }
                WeekStepper(stringResource(R.string.copy_day_to), to, totalWeeks) { to = it }
            }
        },
        confirmButton = {
            TextButton(onClick = { onCopy(from, to) }) {
                Text(stringResource(R.string.copy_day_action))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

/** 周次步进器：标签 + [−] 数值 [＋]。 */
@Composable
private fun WeekStepper(
    label: String,
    value: Int,
    max: Int,
    onChange: (Int) -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        TextButton(onClick = { if (value > 1) onChange(value - 1) }) { Text("−") }
        Text(
            stringResource(R.string.week_label, value),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        TextButton(onClick = { if (value < max) onChange(value + 1) }) { Text("＋") }
    }
}
