package com.campus.schedule.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.campus.schedule.domain.logic.ScheduleConflictDetector
import com.campus.schedule.domain.logic.WeekCalculator
import com.campus.schedule.domain.model.CourseItem
import com.campus.schedule.domain.model.PeriodTemplate
import com.campus.schedule.domain.model.TypeStyle
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/** 周视图：HorizontalPager 翻页切周（自带位移动画），7 列网格 + 节次时间轴，重叠卡片并排。 */
private const val HOUR_HEIGHT_DP = 52
private const val WEEK_BUFFER = 4

@Composable
fun WeekView(
    state: ScheduleUiState,
    viewModel: ScheduleViewModel,
    deletingId: Long?,
    onItemClick: (CourseItem) -> Unit,
    onDeleteComplete: (Long) -> Unit
) {
    val semester = state.semester ?: return
    val pageCount = semester.totalWeeks + WEEK_BUFFER * 2
    val pagerState = rememberPagerState(
        initialPage = state.visibleWeek - 1 + WEEK_BUFFER,
        pageCount = { pageCount }
    )
    LaunchedEffect(pagerState.currentPage) {
        val week = pagerState.currentPage - WEEK_BUFFER + 1
        val monday = WeekCalculator.mondayOf(semester, week)
        if (monday != state.selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))) {
            viewModel.selectDate(monday)
        }
    }

    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
        val week = page - WEEK_BUFFER + 1
        WeekGrid(
            courses = state.courses,
            periods = state.periods,
            typeStyles = state.typeStyles,
            today = state.today,
            totalWeeks = semester.totalWeeks,
            week = week,
            dates = WeekCalculator.datesOfWeek(semester, week),
            deletingId = deletingId,
            onItemClick = onItemClick,
            onDeleteComplete = onDeleteComplete
        )
    }
}

@Composable
private fun WeekGrid(
    courses: List<CourseItem>,
    periods: List<PeriodTemplate>,
    typeStyles: List<TypeStyle>,
    today: LocalDate,
    totalWeeks: Int,
    week: Int,
    dates: List<LocalDate>,
    deletingId: Long?,
    onItemClick: (CourseItem) -> Unit,
    onDeleteComplete: (Long) -> Unit
) {
    val scroll = rememberScrollState()
    val firstStart = periods.firstOrNull()?.startTime ?: 480
    val lastEnd = periods.lastOrNull()?.endTime ?: 1260
    val gridHeight = ((lastEnd - firstStart) / 60f * HOUR_HEIGHT_DP).dp
    val weekLabel = if (week in 1..totalWeeks) "第 $week 周" else ""

    // 本页生效课程 + 按天分组与并排布局，均缓存：翻页切换只重算目标页，不随全局状态反复重建
    val weekItems = remember(courses, week) { courses.filter { it.isEffectiveOn(week) } }
    val dayData: Map<Int, Pair<List<CourseItem>, Map<Long, ScheduleConflictDetector.LanePlacement>>> =
        remember(weekItems) {
            (1..7).associate { day ->
                val items = weekItems.filter { it.dayOfWeek == day }
                day to (items to ScheduleConflictDetector.assignLanes(items))
            }
        }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                weekLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(48.dp)
            )
            dates.forEach { date ->
                val isToday = date == today
                Box(
                    Modifier
                        .weight(1f)
                        .padding(2.dp)
                        .background(
                            if (isToday) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${date.monthValue}/${date.dayOfMonth}",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isToday) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Row(
            Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 8.dp)
        ) {
            // 左侧节次标签：与卡片共用同一套「分钟→像素」映射，绝对定位避免因节次间隙错位
            Box(
                Modifier
                    .width(48.dp)
                    .height(gridHeight)
            ) {
                periods.forEach { period ->
                    Text(
                        "${period.label}\n" + CourseItem.formatMinutes(period.startTime),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = ((period.startTime - firstStart) / 60f * HOUR_HEIGHT_DP).dp)
                    )
                }
            }

            dates.forEachIndexed { index, _ ->
                val day = index + 1
                val (items, lanes) = dayData[day]
                    ?: (emptyList<CourseItem>() to emptyMap<Long, ScheduleConflictDetector.LanePlacement>())
                BoxWithConstraints(
                    Modifier
                        .weight(1f)
                        .height(gridHeight)
                        .padding(1.dp)
                ) {
                    val laneCount = lanes.values.maxOfOrNull { it.laneCount } ?: 1
                    val colWidth = maxWidth / laneCount
                    periods.forEach { period ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .offset(y = ((period.startTime - firstStart) / 60f * HOUR_HEIGHT_DP).dp)
                                .height(1.dp)
                                .background(
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                                )
                        )
                    }
                    items.forEach { item ->
                        val lane = lanes[item.id] ?: return@forEach
                        AnimatedCourseCard(
                            item = item,
                            styles = typeStyles,
                            compact = true,
                            deleting = deletingId == item.id,
                            onClick = { onItemClick(item) },
                            onDeleteComplete = { onDeleteComplete(item.id) },
                            modifier = Modifier
                                .width(colWidth)
                                .offset(
                                    x = colWidth * lane.lane,
                                    y = ((item.startTime - firstStart) / 60f * HOUR_HEIGHT_DP).dp
                                )
                                .height(
                                    ((item.endTime - item.startTime) / 60f * HOUR_HEIGHT_DP).dp
                                )
                        )
                    }
                }
            }
        }
    }
}
