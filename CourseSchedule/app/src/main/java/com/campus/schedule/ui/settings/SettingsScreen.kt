package com.campus.schedule.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.campus.schedule.R
import com.campus.schedule.domain.model.AppLanguage
import com.campus.schedule.domain.model.InactiveWeekPolicy
import com.campus.schedule.domain.model.ThemeMode
import com.campus.schedule.domain.model.ViewMode
import com.campus.schedule.ui.settings.SettingsSections

/**
 * 设置页：外观 / 学期 / 课表数据 / 提醒勿扰 / 伙伴课表。
 * 语言切换使用 appcompat per-app language（自动持久化，重启生效）。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val semester by viewModel.semester.collectAsState()
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_settings)) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(stringResource(R.string.action_back)) }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item { SectionTitle(stringResource(R.string.settings_section_appearance)) }
            item {
                SettingChips(
                    title = stringResource(R.string.setting_theme),
                    options = listOf(
                        stringResource(R.string.theme_light),
                        stringResource(R.string.theme_dark),
                        stringResource(R.string.theme_system)
                    ),
                    selectedIndex = when (settings.themeMode) {
                        ThemeMode.LIGHT -> 0
                        ThemeMode.DARK -> 1
                        ThemeMode.SYSTEM -> 2
                    }
                ) { index ->
                    viewModel.setThemeMode(
                        when (index) {
                            0 -> ThemeMode.LIGHT
                            1 -> ThemeMode.DARK
                            else -> ThemeMode.SYSTEM
                        }
                    )
                }
            }
            item {
                SettingChips(
                    title = stringResource(R.string.setting_language),
                    options = listOf(
                        stringResource(R.string.language_zh_cn),
                        stringResource(R.string.language_zh_tw),
                        stringResource(R.string.language_en),
                        stringResource(R.string.language_follow_system)
                    ),
                    selectedIndex = when (settings.language) {
                        AppLanguage.ZH_CN -> 0
                        AppLanguage.ZH_TW -> 1
                        AppLanguage.EN -> 2
                        else -> 3
                    }
                ) { index ->
                    val tag = when (index) {
                        0 -> "zh-CN"
                        1 -> "zh-TW"
                        2 -> "en"
                        else -> ""
                    }
                    viewModel.setLanguage(tag)
                    AppCompatDelegate.setApplicationLocales(
                        if (tag.isEmpty()) LocaleListCompat.getEmptyLocaleList()
                        else LocaleListCompat.forLanguageTags(tag)
                    )
                }
            }
            item {
                SettingSwitch(
                    title = stringResource(R.string.setting_dynamic_color),
                    subtitle = stringResource(R.string.setting_dynamic_color_desc),
                    checked = settings.dynamicColorEnabled
                ) { viewModel.setDynamicColor(it) }
            }
            item {
                SettingChips(
                    title = stringResource(R.string.setting_default_view),
                    options = listOf(
                        stringResource(R.string.view_week),
                        stringResource(R.string.view_day)
                    ),
                    selectedIndex = if (settings.defaultView == ViewMode.WEEK) 0 else 1
                ) { index ->
                    viewModel.setDefaultView(if (index == 0) ViewMode.WEEK else ViewMode.DAY)
                }
            }
            item {
                SettingChips(
                    title = stringResource(R.string.setting_inactive_week),
                    options = listOf(
                        stringResource(R.string.inactive_dim),
                        stringResource(R.string.inactive_hide),
                        stringResource(R.string.inactive_placeholder)
                    ),
                    selectedIndex = when (settings.inactiveWeekPolicy) {
                        InactiveWeekPolicy.DIM -> 0
                        InactiveWeekPolicy.HIDE -> 1
                        InactiveWeekPolicy.PLACEHOLDER -> 2
                    }
                ) { index ->
                    viewModel.setInactiveWeekPolicy(
                        when (index) {
                            0 -> InactiveWeekPolicy.DIM
                            1 -> InactiveWeekPolicy.HIDE
                            else -> InactiveWeekPolicy.PLACEHOLDER
                        }
                    )
                }
            }
            item {
                SettingSwitch(
                    title = stringResource(R.string.setting_show_countdown),
                    checked = settings.showCountdown
                ) { viewModel.setShowCountdown(it) }
            }

            item { SectionTitle(stringResource(R.string.settings_section_semester)) }
            item {
                SettingsSections.SemesterSection(
                    semester = semester,
                    onStartDate = viewModel::setSemesterStart,
                    onTotalWeeks = viewModel::setTotalWeeks
                )
            }

            item { SectionTitle(stringResource(R.string.settings_section_schedule_data)) }
            item { SettingsSections.DataSection(viewModel) }

            item { SectionTitle(stringResource(R.string.settings_section_reminder)) }
            item { SettingsSections.ReminderSection(viewModel, context) }

            item { SectionTitle(stringResource(R.string.settings_section_partner)) }
            item { SettingsSections.PartnerSection(viewModel) }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingSwitch(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun SettingChips(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelect(index) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                )
            }
        }
    }
}
