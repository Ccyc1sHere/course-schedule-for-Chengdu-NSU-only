package com.campus.schedule.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.campus.schedule.domain.model.TypeStyle
import com.campus.schedule.ui.components.displayLabel

/**
 * 类型选择器：横排胶囊，显示名来自 TypeStyle（用户可自定义）。
 */
@Composable
fun TypePicker(
    typeStyles: List<TypeStyle>,
    selectedKey: String,
    onSelect: (String) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        typeStyles.forEach { style ->
            val selected = style.key == selectedKey
            Text(
                text = style.displayLabel(),
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelect(style.key) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            )
        }
    }
}

/**
 * 星期选择器：1..7 横排，选中态使用主题色。
 */
@Composable
fun DayPickerRow(
    selected: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        (1..7).forEach { day ->
            val isSelected = day == selected
            Text(
                text = weekdayLabel(day),
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelect(day) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            )
        }
    }
}

/** 星期中文短名（用户数据用阿拉伯数字，这里展示为「一…日」）。 */
private fun weekdayLabel(day: Int): String = when (day) {
    1 -> "一"; 2 -> "二"; 3 -> "三"; 4 -> "四"
    5 -> "五"; 6 -> "六"; else -> "日"
}
