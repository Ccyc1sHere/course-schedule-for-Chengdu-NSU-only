package com.campus.schedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.campus.schedule.ui.editor.EditorScreen
import com.campus.schedule.ui.editor.EditorViewModel
import com.campus.schedule.ui.schedule.ScheduleScreen
import com.campus.schedule.ui.schedule.ScheduleViewModel
import com.campus.schedule.ui.settings.SettingsScreen
import com.campus.schedule.ui.settings.SettingsViewModel

/**
 * 导航图：主界面 ↔ 编辑页 ↔ 设置页。
 * 编辑页 / 设置页均为二级页面，系统返回保持默认行为（编辑页返回时弹自绘确认对话框）。
 */
@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    val container = rememberAppContainer()

    NavHost(navController = navController, startDestination = Screen.Schedule.route) {
        composable(Screen.Schedule.route) {
            val vm: ScheduleViewModel = viewModel { ScheduleViewModel(container.repository) }
            ScheduleScreen(
                viewModel = vm,
                onEditItem = { id, day -> navController.navigate(Screen.Editor.build(id, day)) },
                onOpenSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(
            route = Screen.Editor.route,
            arguments = listOf(
                navArgument(Screen.Editor.ARG_ITEM_ID) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument(Screen.Editor.ARG_DAY) {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ) { entry ->
            val itemId = entry.arguments?.getLong(Screen.Editor.ARG_ITEM_ID)?.takeIf { it > 0 }
            val day = entry.arguments?.getInt(Screen.Editor.ARG_DAY) ?: 1
            val vm: EditorViewModel = viewModel { EditorViewModel(container.repository) }
            EditorScreen(
                viewModel = vm,
                itemId = itemId,
                initialDay = day,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            val vm: SettingsViewModel = viewModel { SettingsViewModel(container.repository) }
            SettingsScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
    }
}
