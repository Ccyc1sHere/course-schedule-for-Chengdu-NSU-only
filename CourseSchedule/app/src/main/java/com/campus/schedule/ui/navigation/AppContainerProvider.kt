package com.campus.schedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.campus.schedule.AppContainer
import com.campus.schedule.ScheduleApp

/** 在 Compose 中获取应用级依赖容器（Application 持有，进程内单例）。 */
@Composable
fun rememberAppContainer(): AppContainer {
    val app = LocalContext.current.applicationContext
    return (app as ScheduleApp).container
}
