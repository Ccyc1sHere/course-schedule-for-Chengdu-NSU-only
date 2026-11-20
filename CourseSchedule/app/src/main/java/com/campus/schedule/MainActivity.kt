package com.campus.schedule

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.campus.schedule.domain.model.AppSettings
import com.campus.schedule.ui.navigation.AppNavGraph
import com.campus.schedule.ui.theme.CourseScheduleTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * 单 Activity 宿主。使用 AppCompatActivity 以支持应用内三语切换（per-app language）。
 * Edge-to-Edge：状态栏 / 导航栏透明，图标颜色随主题切换（浅色主题用深色图标）。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var settingsFlow: StateFlow<AppSettings>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as ScheduleApp).container
        settingsFlow = container.repository.settings.stateIn(
            lifecycleScope,
            SharingStarted.Eagerly,
            AppSettings()
        )

        setContent {
            val settings by settingsFlow.collectAsState()
            CourseScheduleTheme(
                themeMode = settings.themeMode,
                dynamicColorEnabled = settings.dynamicColorEnabled
            ) {
                AppNavGraph()
            }
        }
    }
}
