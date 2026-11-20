package com.campus.schedule.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.campus.schedule.R
import com.campus.schedule.domain.model.CourseItem
import com.campus.schedule.domain.model.PeriodTemplate
import com.campus.schedule.domain.model.TypeStyle

/**
 * 节次表编辑器：列出全部节次，label / 起止时间均可修改（保存时整体提交）。
 */
@Composable
fun PeriodEditorDialog(
    periods: List<PeriodTemplate>,
    onSave: (List<PeriodTemplate>) -> Unit,
    onDismiss: () -> Unit
) {
    val drafts = remember(periods) {
        periods.map { p ->
            mutableStateOf(
                Triple(p.label, CourseItem.formatMinutes(p.startTime), CourseItem.formatMinutes(p.endTime))
            )
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(stringResource(R.string.setting_period_templates)) },
        text = {
            LazyColumn {
                items(periods.indices.toList()) { i ->
                    val d = drafts[i]
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = d.value.first,
                            onValueChange = { d.value = d.value.copy(first = it) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = d.value.second,
                            onValueChange = { d.value = d.value.copy(second = it) },
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = d.value.third,
                            onValueChange = { d.value = d.value.copy(third = it) },
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(periods.mapIndexed { i, p ->
                    p.copy(
                        label = drafts[i].value.first.ifBlank { p.label },
                        startTime = CourseItem.parseMinutes(drafts[i].value.second) ?: p.startTime,
                        endTime = CourseItem.parseMinutes(drafts[i].value.third) ?: p.endTime
                    )
                })
            }) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

/**
 * 类型外观编辑器：显示名可改（留空即用内置三语名），颜色可选。
 */
@Composable
fun TypeEditorDialog(
    typeStyles: List<TypeStyle>,
    onSave: (List<TypeStyle>) -> Unit,
    onDismiss: () -> Unit
) {
    val nameStates = remember(typeStyles) {
        typeStyles.map { mutableStateOf(it.displayName) }
    }
    val colorStates = remember(typeStyles) {
        typeStyles.map { mutableStateOf(it.colorArgb) }
    }
    val palette = listOf<Long>(
        0xFF2C6BE5, 0xFF8E5AE8, 0xFF2FA96B, 0xFFE8862C,
        0xFF2CA8B8, 0xFFE05A5A, 0xFF6B7280, 0xFFB4643C
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(stringResource(R.string.setting_type_styles)) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(typeStyles.indices.toList()) { i ->
                    val style = typeStyles[i]
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(colorStates[i].value))
                        )
                        OutlinedTextField(
                            value = nameStates[i].value,
                            onValueChange = { nameStates[i].value = it },
                            placeholder = { Text(style.key) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        palette.forEach { argb ->
                            Box(
                                Modifier
                                    .size(18.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(argb))
                                    .clickable { colorStates[i].value = argb }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(typeStyles.mapIndexed { i, s ->
                    s.copy(
                        displayName = nameStates[i].value,
                        colorArgb = colorStates[i].value
                    )
                })
            }) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
