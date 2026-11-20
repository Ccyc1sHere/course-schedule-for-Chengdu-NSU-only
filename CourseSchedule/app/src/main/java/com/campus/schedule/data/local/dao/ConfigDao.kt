package com.campus.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.campus.schedule.data.local.entity.AppSettingsEntity
import com.campus.schedule.data.local.entity.PeriodTemplateEntity
import com.campus.schedule.data.local.entity.SemesterConfigEntity
import com.campus.schedule.data.local.entity.TypeStyleEntity
import kotlinx.coroutines.flow.Flow

/** 配置类 DAO：学期 / 节次 / 类型外观 / 应用设置。全部允许用户编辑。 */
@Dao
interface ConfigDao {

    // ---------- 学期 ----------
    @Query("SELECT * FROM semester_config WHERE id = 1")
    fun observeSemester(): Flow<SemesterConfigEntity?>

    @Query("SELECT * FROM semester_config WHERE id = 1")
    suspend fun getSemester(): SemesterConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSemester(config: SemesterConfigEntity)

    // ---------- 节次 ----------
    @Query("SELECT * FROM period_templates ORDER BY `index`")
    fun observePeriods(): Flow<List<PeriodTemplateEntity>>

    @Query("SELECT * FROM period_templates ORDER BY `index`")
    suspend fun getPeriods(): List<PeriodTemplateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPeriods(periods: List<PeriodTemplateEntity>)

    // ---------- 类型外观 ----------
    @Query("SELECT * FROM type_styles ORDER BY sort_order")
    fun observeTypeStyles(): Flow<List<TypeStyleEntity>>

    @Query("SELECT * FROM type_styles ORDER BY sort_order")
    suspend fun getTypeStyles(): List<TypeStyleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTypeStyles(styles: List<TypeStyleEntity>)

    // ---------- 应用设置 ----------
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun observeSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1")
    suspend fun getSettings(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(settings: AppSettingsEntity)

    /** 重置为默认设置（保留 seed_imported 标记，避免重复灌示例数据）。 */
    @Transaction
    suspend fun resetSettings() {
        upsertSettings(
            AppSettingsEntity(id = 1, seedImported = getSettings()?.seedImported ?: false)
        )
    }
}
