package com.campus.schedule.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.campus.schedule.domain.model.CourseItem
import com.campus.schedule.domain.model.TypeStyle

/**
 * 日程色块卡片（周 / 日视图共用）。
 *
 * @param dimmed 非本周生效时的视觉降级（低透明度 + 「非本周」角标由外层叠加）
 * @param highlight 正在进行 / 即将开始的强调态
 * @param compact 周视图中的紧凑布局
 */
@Composable
fun CourseCard(
    item: CourseItem,
    styles: List<TypeStyle>,
    modifier: Modifier = Modifier,
    dimmed: Boolean = false,
    highlight: Boolean = false,
    compact: Boolean = false,
    onClick: () -> Unit
) {
    val color = resolveColor(item, styles)
    val style = styles.firstOrNull { it.key == item.typeKey }
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = if (dimmed) color.copy(alpha = 0.4f) else color,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = if (highlight) 3.dp else 0.dp
    ) {
        Column(Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = iconForKey(style?.iconKey ?: "LABEL"),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(if (compact) 13.dp else 16.dp)
                )
                Text(
                    text = item.title,
                    style = if (compact) {
                        androidx.compose.material3.MaterialTheme.typography.labelSmall
                    } else {
                        androidx.compose.material3.MaterialTheme.typography.titleSmall
                    },
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            if (compact) {
                val meta = listOf(item.location, item.teacher).filter { it.isNotBlank() }
                    .joinToString(" · ")
                if (meta.isNotBlank()) {
                    Text(
                        text = meta,
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            } else {
                Text(
                    text = "${item.startTimeText} - ${item.endTimeText}",
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.95f),
                    modifier = Modifier.padding(top = 2.dp)
                )
                val meta = listOf(item.location, item.teacher).filter { it.isNotBlank() }
                    .joinToString(" · ")
                if (meta.isNotBlank()) {
                    Text(
                        text = meta,
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
