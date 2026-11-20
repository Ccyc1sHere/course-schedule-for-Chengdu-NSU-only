package com.campus.schedule.ui.editor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.campus.schedule.R

/**
 * 编辑页：新增 / 编辑日程的完整表单。
 * 系统返回不拦截：若有未保存修改，先弹自绘 M3 确认对话框（与主题一致）。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    itemId: Long?,
    initialDay: Int,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val meta by viewModel.meta.collectAsState()
    var showDiscard by remember { mutableStateOf(false) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    LaunchedEffect(itemId, initialDay) { viewModel.load(itemId, initialDay) }

    fun requestBack() {
        if (state.hasChanges && !state.saved) showDiscard = true else onBack()
    }
    BackHandler { requestBack() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (state.isNew) R.string.add_item else R.string.edit_item))
                },
                navigationIcon = {
                    TextButton(onClick = ::requestBack) {
                        Text(stringResource(R.string.action_back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::setTitle,
                label = { Text(stringResource(R.string.field_title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            TypePicker(meta.typeStyles, state.typeKey) { viewModel.setTypeKey(it) }
            DayPickerRow(state.dayOfWeek) { viewModel.setDayOfWeek(it) }
            TimeRow(
                state = state,
                viewModel = viewModel,
                onPickStart = { showStartPicker = true },
                onPickEnd = { showEndPicker = true }
            )
            if (state.timeInvalid) {
                Text(
                    stringResource(R.string.field_time_invalid),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            PeriodQuickFill(meta.periods) { viewModel.applyPeriod(it) }
            OutlinedTextField(
                value = state.location,
                onValueChange = viewModel::setLocation,
                label = { Text(stringResource(R.string.field_location)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = state.teacher,
                onValueChange = viewModel::setTeacher,
                label = { Text(stringResource(R.string.field_teacher)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = state.note,
                onValueChange = viewModel::setNote,
                label = { Text(stringResource(R.string.field_note)) },
                modifier = Modifier.fillMaxWidth()
            )
            WeekRangePicker(
                totalWeeks = meta.totalWeeks,
                weekStart = state.weekStart,
                weekEnd = state.weekEnd,
                onStart = viewModel::setWeekStart,
                onEnd = viewModel::setWeekEnd
            )
            ParityPicker(state.weekParity) { viewModel.setWeekParity(it) }
            ColorPicker(meta.typeStyles, state.colorArgb) { viewModel.setColorArgb(it) }
            Button(
                onClick = { viewModel.save { onBack() } },
                enabled = !state.timeInvalid && state.title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) { Text(stringResource(R.string.save)) }
        }
    }

    if (showStartPicker) {
        TimePickerDialogM3(state.startTime) {
            viewModel.setStartTime(it)
            showStartPicker = false
        }
    }
    if (showEndPicker) {
        TimePickerDialogM3(state.endTime) {
            viewModel.setEndTime(it)
            showEndPicker = false
        }
    }
    if (showDiscard) {
        DiscardChangesDialog(
            onConfirmDiscard = { showDiscard = false; onBack() },
            onCancel = { showDiscard = false }
        )
    }
}
