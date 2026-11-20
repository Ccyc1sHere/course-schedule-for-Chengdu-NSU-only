package com.campus.schedule.ui.navigation

/**
 * 导航路由定义。
 *
 * @property route 路由字符串；带参数的路由使用占位符，实际跳转时用 [build] 生成。
 */
sealed class Screen(val route: String) {

    /** 主界面：日 / 周视图 + 倒计时条。 */
    data object Schedule : Screen("schedule")

    /** 编辑页：itemId 为空表示新增；day 为预选星期（新增时使用）。 */
    data object Editor : Screen("editor?itemId={itemId}&day={day}") {
        const val ARG_ITEM_ID = "itemId"
        const val ARG_DAY = "day"

        fun build(itemId: Long? = null, day: Int = 1): String =
            "editor?itemId=${itemId ?: -1L}&day=$day"
    }

    /** 设置页。 */
    data object Settings : Screen("settings")
}
