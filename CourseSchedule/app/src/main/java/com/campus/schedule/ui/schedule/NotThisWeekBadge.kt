package com.campus.schedule.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.campus.schedule.R

/** 「非本周」角标：低透明度卡片右上角的提示标签。 */
@Composable
fun NotThisWeekBadge(modifier: Modifier = Modifier) {
    Text(
        stringResource(R.string.not_this_week),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}
