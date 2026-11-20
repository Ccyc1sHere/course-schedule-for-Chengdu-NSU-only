package com.campus.schedule.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.campus.schedule.R
import com.campus.schedule.domain.model.PeriodTemplate
import com.campus.schedule.domain.model.TypeStyle
import com.campus.schedule.domain.model.WeekParity
import com.campus.schedule.ui.components.displayLabel

/**
 * 周次范围选择：RangeSlider，1..totalWeeks。
 */
@Composable
fun WeekRangePicker(
    totalWeeks: Int,
    weekStart: Int,
    weekEnd: Int,
    onStart: (Int) -> Unit,
    onEnd: (Int) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            "${stringResource(R.string.field_week_range)}：第 $weekStart-$weekEnd 周",
            style = MaterialTheme.typography.bodyMedium
        )
        RangeSlider(
            value = weekStart.toFloat()..weekEnd.toFloat(),
            onValueChange = { range ->
                onStart(range.start.toInt())
                onEnd(range.endInclusive.toInt())
            },
            valueRange = 1f..totalWeeks.toFloat(),
            steps = (totalWeeks - 2).coerceAtLeast(0)
        )
    }
}

/**
 * 单双周选择：每周 / 仅单周 / 仅双周。
 */
@Composable
fun ParityPicker(
    selected: WeekParity,
    onSelect: (WeekParity) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            WeekParity.ALL to stringResource(R.string.field_week_all),
            WeekParity.ODD to stringResource(R.string.field_week_odd),
            WeekParity.EVEN to stringResource(R.string.field_week_even)
        ).forEach { (parity, label) ->
            val isSelected = parity == selected
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelect(parity) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

/**
 * 颜色选择：首项「跟随类型默认色」，其后为固定色板。
 */
@Composable
fun ColorPicker(
    typeStyles: List<TypeStyle>,
    selected: Long?,
    onSelect: (Long?) -> Unit
) {
    val palette = listOf<Long>(
        0xFF2C6BE5, 0xFF8E5AE8, 0xFF2FA96B, 0xFFE8862C,
        0xFF2CA8B8, 0xFFE05A5A, 0xFF6B7280, 0xFFB4643C
    )
    Column(Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.field_color), style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // 跟随类型默认色
            ColorSwatch(
                color = null,
                isSelected = selected == null,
                label = stringResource(R.string.field_color_follow_type)
            ) { onSelect(null) }
            palette.forEach { argb ->
                ColorSwatch(Color(argb), selected == argb, null) { onSelect(argb) }
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    color: Color?,
    isSelected: Boolean,
    label: String?,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color ?: MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (label != null) {
            Text("A", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else if (isSelected) {
            Text("✓", color = Color.White, style = MaterialTheme.typography.labelMedium)
        }
    }
}

/**
 * 按节次快速填充起止时间（节次表可在设置页自定义）。
 */
@Composable
fun PeriodQuickFill(
    periods: List<PeriodTemplate>,
    onSelect: (PeriodTemplate) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        periods.forEach { period ->
            Text(
                text = period.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelect(period) }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}
