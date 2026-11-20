package com.campus.schedule.ui.schedule

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.campus.schedule.domain.model.CourseItem
import com.campus.schedule.domain.model.TypeStyle
import com.campus.schedule.ui.components.CourseCard

/**
 * 带删除动画的日程卡片：deleting 为 true 时先播放收缩 + 淡出（约 220ms），
 * 动画结束后才回调 [onDeleteComplete]（由外层提交数据库删除），避免列表闪跳。
 */
@Composable
fun AnimatedCourseCard(
    item: CourseItem,
    styles: List<TypeStyle>,
    modifier: Modifier = Modifier,
    dimmed: Boolean = false,
    highlight: Boolean = false,
    compact: Boolean = false,
    deleting: Boolean = false,
    onClick: () -> Unit,
    onDeleteComplete: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (deleting) 0.2f else 1f,
        animationSpec = tween(durationMillis = 220),
        label = "deleteScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (deleting) 0f else 1f,
        animationSpec = tween(durationMillis = 220),
        label = "deleteAlpha"
    )
    LaunchedEffect(scale, deleting) {
        if (deleting && scale <= 0.21f) onDeleteComplete()
    }
    Box(
        modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        }
    ) {
        CourseCard(
            item = item,
            styles = styles,
            dimmed = dimmed,
            highlight = highlight,
            compact = compact,
            onClick = onClick
        )
    }
}
