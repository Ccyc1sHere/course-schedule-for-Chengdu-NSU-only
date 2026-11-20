package com.campus.schedule.ui.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.campus.schedule.domain.model.SemesterConfig
import com.campus.schedule.util.ShareCode
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * 设置页各分组内容的实现（学期 / 课表数据 / 提醒勿扰 / 伙伴课表）。
 * 作为单例对象提供分组，供 SettingsScreen 调用。
 */
object SettingsSections {

    /** 学期起始日期 + 总周数。 */
    @Composable
    fun SemesterSection(
        semester: SemesterConfig?,
        onStartDate: (LocalDate) -> Unit,
        onTotalWeeks: (Int) -> Unit
    ) {
        var showDatePicker by remember { mutableStateOf(false) }
        val current = semester ?: SemesterConfig(LocalDate.now(), 18)
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    "${stringResource(R.string.setting_semester_start)}：${current.startDate}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { showDatePicker = true }) {
                    Text(stringResource(R.string.action_edit))
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Text(
                    stringResource(R.string.setting_total_weeks),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { onTotalWeeks((current.totalWeeks - 1).coerceAtLeast(1)) }) {
                    Text("−")
                }
                Text("${current.totalWeeks}", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { onTotalWeeks((current.totalWeeks + 1).coerceAtMost(30)) }) {
                    Text("＋")
                }
            }
        }
        if (showDatePicker) {
            SemesterDatePickerDialog(current.startDate) {
                onStartDate(it)
                showDatePicker = false
            }
        }
    }

    /** 课表数据：节次表 / 类型外观 / 示例数据管理。 */
    @Composable
    fun DataSection(viewModel: SettingsViewModel) {
        val periods by viewModel.periods.collectAsState()
        val types by viewModel.typeStyles.collectAsState()
        var showPeriodEditor by remember { mutableStateOf(false) }
        var showTypeEditor by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { showPeriodEditor = true }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.setting_period_templates))
            }
            OutlinedButton(onClick = { showTypeEditor = true }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.setting_type_styles))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { viewModel.clearSeedCourses() }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.setting_seed_clear))
                }
                OutlinedButton(onClick = { viewModel.restoreSeedCourses() }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.setting_seed_restore))
                }
            }
        }
        if (showPeriodEditor) {
            PeriodEditorDialog(periods, { viewModel.upsertPeriods(it); showPeriodEditor = false }) {
                showPeriodEditor = false
            }
        }
        if (showTypeEditor) {
            TypeEditorDialog(types, { viewModel.upsertTypeStyles(it); showTypeEditor = false }) {
                showTypeEditor = false
            }
        }
    }

    /** 提醒与勿扰：课前提醒（13+ 权限）、勿扰权限引导。 */
    @Composable
    fun ReminderSection(viewModel: SettingsViewModel, context: Context) {
        val settings by viewModel.settings.collectAsState()
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (!granted) viewModel.setNotificationDeniedOnce()
        }
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.setting_reminder), style = MaterialTheme.typography.bodyLarge)
                    Text(
                        stringResource(R.string.setting_reminder_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.reminderEnabled,
                    onCheckedChange = {
                        viewModel.setReminderEnabled(it)
                        viewModel.applyReminderSchedule(context, it)
                    }
                )
            }
            if (settings.reminderEnabled) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.setting_countdown_minutes),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.setReminderMinutes(settings.reminderMinutesBefore - 5) }) {
                        Text("−")
                    }
                    Text("${settings.reminderMinutesBefore}", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { viewModel.setReminderMinutes(settings.reminderMinutesBefore + 5) }) {
                        Text("＋")
                    }
                }
                if (Build.VERSION.SDK_INT >= 33 && !settings.notificationDeniedOnce) {
                    OutlinedButton(
                        onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(stringResource(R.string.setting_notification_permission)) }
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.setting_dnd), style = MaterialTheme.typography.bodyLarge)
                    Text(
                        stringResource(R.string.setting_dnd_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = settings.dndEnabled, onCheckedChange = { viewModel.setDndEnabled(it) })
            }
            if (settings.dndEnabled && !viewModel.isDndAccessGranted(context)) {
                OutlinedButton(
                    onClick = {
                        context.startActivity(
                            Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(R.string.setting_dnd_permission)) }
            }
        }
    }

    /** 伙伴课表：显示开关 + 导入分享码 + 导出 + 清除。 */
    @Composable
    fun PartnerSection(viewModel: SettingsViewModel) {
        val settings by viewModel.settings.collectAsState()
        var importDialog by remember { mutableStateOf(false) }
        var shareCode by remember { mutableStateOf<String?>(null) }
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.setting_partner_overlay),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = settings.partnerOverlayEnabled,
                    onCheckedChange = { viewModel.setPartnerOverlay(it) }
                )
            }
            OutlinedButton(onClick = { importDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.setting_partner_paste))
            }
            OutlinedButton(
                onClick = { viewModel.exportShareCode { shareCode = it } },
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.setting_export)) }
            OutlinedButton(onClick = { viewModel.clearPartner() }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.setting_partner_clear))
            }
        }
        if (importDialog) {
            PartnerImportDialog(
                onImport = { code ->
                    val slots = ShareCode.decodeBusySlots(code)
                    if (slots != null) {
                        viewModel.savePartner("partner", ShareCode.slotsToJson(slots))
                    }
                    importDialog = false
                },
                onDismiss = { importDialog = false }
            )
        }
        shareCode?.let { code ->
            AlertDialog(
                onDismissRequest = { shareCode = null },
                containerColor = MaterialTheme.colorScheme.surface,
                title = { Text(stringResource(R.string.setting_export)) },
                text = { Text(code, style = MaterialTheme.typography.bodySmall) },
                confirmButton = {
                    TextButton(onClick = { shareCode = null }) {
                        Text(stringResource(R.string.action_close))
                    }
                }
            )
        }
    }
}

/** 粘贴分享码导入伙伴课表。 */
@Composable
private fun PartnerImportDialog(
    onImport: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(stringResource(R.string.setting_partner_paste)) },
        text = {
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("CS1:…") }
            )
        },
        confirmButton = {
            TextButton(onClick = { onImport(code.trim()) }) { Text(stringResource(R.string.confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

/** M3 DatePicker 包裹在 AlertDialog，主题色一致。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SemesterDatePickerDialog(
    initial: LocalDate,
    onConfirm: (LocalDate) -> Unit
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initial.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    )
    AlertDialog(
        onDismissRequest = { onConfirm(initial) },
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(stringResource(R.string.setting_semester_start)) },
        text = { DatePicker(state = state) },
        confirmButton = {
            TextButton(onClick = {
                val millis = state.selectedDateMillis ?: return@TextButton
                onConfirm(
                    Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                )
            }) { Text(stringResource(R.string.confirm)) }
        },
        dismissButton = {
            TextButton(onClick = { onConfirm(initial) }) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
