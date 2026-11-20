package com.campus.schedule.data.repository

import com.campus.schedule.data.local.AppDatabase
import com.campus.schedule.data.local.dao.ConfigDao
import com.campus.schedule.data.local.dao.CourseDao
import com.campus.schedule.data.local.dao.PartnerDao
import com.campus.schedule.data.local.entity.AppSettingsEntity
import com.campus.schedule.data.local.entity.CourseItemEntity
import com.campus.schedule.data.local.entity.PartnerScheduleEntity
import com.campus.schedule.data.local.entity.PeriodTemplateEntity
import com.campus.schedule.data.local.entity.SemesterConfigEntity
import com.campus.schedule.data.local.entity.TypeStyleEntity
import com.campus.schedule.data.local.toDomain
import com.campus.schedule.data.local.toEntity
import com.campus.schedule.data.seed.SeedData
import com.campus.schedule.domain.model.AppSettings
import com.campus.schedule.domain.model.CourseItem
import com.campus.schedule.domain.model.PartnerSchedule
import com.campus.schedule.domain.model.PeriodTemplate
import com.campus.schedule.domain.model.SemesterConfig
import com.campus.schedule.domain.model.TypeStyle
import com.campus.schedule.domain.model.WeekParity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * 单一数据源仓库：对 ViewModel 只暴露领域模型与 Flow。
 *
 * 数据全在本机 Room 中，不联网、不注册。示例数据仅首次启动导入一次，
 * 用户清空后不会再次自动灌入（由 `app_settings.seed_imported` 标记）。
 */
class ScheduleRepository(
    private val courseDao: CourseDao,
    private val configDao: ConfigDao,
    private val partnerDao: PartnerDao
) {

    // ---------- 观察流 ----------
    val courses: Flow<List<CourseItem>> = courseDao.observeAll().map { list -> list.map { it.toDomain() } }
    val semester: Flow<SemesterConfig?> = configDao.observeSemester().map { it?.toDomain() }
    val periods: Flow<List<PeriodTemplate>> = configDao.observePeriods().map { l -> l.map { it.toDomain() } }
    val typeStyles: Flow<List<TypeStyle>> = configDao.observeTypeStyles().map { l -> l.map { it.toDomain() } }
    val settings: Flow<AppSettings> = configDao.observeSettings().map { it?.toDomain() ?: AppSettings() }

    /** 聚合快照：供 Widget / 主界面一次读取。 */
    val snapshot: Flow<ScheduleSnapshot> = combine(
        courses, semester, periods, typeStyles, settings
    ) { c, s, p, t, a ->
        ScheduleSnapshot(c, s, p, t, a)
    }

    // ---------- 日程 CRUD ----------
    suspend fun upsertCourse(item: CourseItem) = courseDao.upsert(item.toEntity())
    suspend fun getCourse(id: Long): CourseItem? = courseDao.getById(id)?.toDomain()
    suspend fun deleteCourse(id: Long) = courseDao.deleteById(id)
    suspend fun deleteAllCourses() = courseDao.deleteAll()
    suspend fun updateSortOrders(items: List<CourseItem>) =
        courseDao.updateSortOrders(items.map { it.toEntity() })

    /**
     * 整周复制：把在 [fromWeek] 生效的全部日程，复制一份到 [toWeek]（单周生效、不保留单双周）。
     * 解决「第 N 周课表和上一周几乎一样」的反复录入痛点。
     * @return 复制的条数。
     */
    suspend fun copyWeek(fromWeek: Int, toWeek: Int): Int {
        val source = courseDao.getAll().map { it.toDomain() }.filter { it.isEffectiveOn(fromWeek) }
        val copied = source.map {
            it.copy(
                id = 0,
                weekStart = toWeek,
                weekEnd = toWeek,
                weekParity = WeekParity.ALL,
                createdAt = System.currentTimeMillis()
            )
        }
        courseDao.insertAll(copied.map { it.toEntity() })
        return copied.size
    }

    // ---------- 配置 ----------
    suspend fun upsertSemester(config: SemesterConfig) = configDao.upsertSemester(config.toEntity())
    suspend fun upsertPeriods(list: List<PeriodTemplate>) = configDao.upsertPeriods(list.map { it.toEntity() })
    suspend fun upsertTypeStyles(list: List<TypeStyle>) = configDao.upsertTypeStyles(list.map { it.toEntity() })
    suspend fun upsertSettings(settings: AppSettings) = configDao.upsertSettings(settings.toEntity())

    // ---------- 伙伴课表 ----------
    fun observePartner(): Flow<PartnerScheduleEntity?> = partnerDao.observePartner()
    suspend fun savePartner(partner: PartnerSchedule, slotsJson: String) =
        partnerDao.upsertPartner(
            PartnerScheduleEntity(name = partner.name, slotsJson = slotsJson, importedAt = partner.importedAt)
        )
    suspend fun clearPartner() = partnerDao.clearPartner()

    // ---------- 初始化 ----------
    /**
     * 首次启动写入默认学期 / 节次 / 类型外观 / 设置与示例课表。
     * 重复调用安全；用户清空示例数据后 seed_imported 仍为 true，不会重复灌入。
     */
    suspend fun ensureSeeded() {
        val settings = configDao.getSettings()
        if (settings != null) return
        configDao.upsertSemester(SeedData.semester)
        configDao.upsertPeriods(SeedData.periods)
        configDao.upsertTypeStyles(SeedData.typeStyles)
        configDao.upsertSettings(SeedData.settings.copy(seedImported = true))
        courseDao.insertAll(SeedData.courses)
    }

    /** 重新导入示例课表（先清空全部日程）。 */
    suspend fun restoreSeedCourses() {
        courseDao.deleteAll()
        courseDao.insertAll(SeedData.courses)
    }

    companion object {
        /** 从 Application 的容器构造。 */
        fun from(db: AppDatabase): ScheduleRepository =
            ScheduleRepository(db.courseDao(), db.configDao(), db.partnerDao())
    }
}

/** 聚合快照：Widget 与主界面共用。 */
data class ScheduleSnapshot(
    val courses: List<CourseItem>,
    val semester: SemesterConfig?,
    val periods: List<PeriodTemplate>,
    val typeStyles: List<TypeStyle>,
    val settings: AppSettings
)
