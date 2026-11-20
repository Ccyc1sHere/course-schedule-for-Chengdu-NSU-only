package com.campus.schedule

import android.app.Application
import com.campus.schedule.data.local.AppDatabase
import com.campus.schedule.data.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 简易依赖容器（不引入 DI 框架，保持轻量）。
 */
class AppContainer(app: Application) {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val database: AppDatabase = AppDatabase.getInstance(app)
    val repository: ScheduleRepository = ScheduleRepository.from(database)
}

/**
 * 应用入口：初始化数据库容器并异步导入首次启动的示例数据。
 */
class ScheduleApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.applicationScope.launch { container.repository.ensureSeeded() }
    }
}
