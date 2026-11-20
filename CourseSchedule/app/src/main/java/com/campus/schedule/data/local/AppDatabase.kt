package com.campus.schedule.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.campus.schedule.data.local.dao.ConfigDao
import com.campus.schedule.data.local.dao.CourseDao
import com.campus.schedule.data.local.dao.PartnerDao
import com.campus.schedule.data.local.entity.AppSettingsEntity
import com.campus.schedule.data.local.entity.CourseItemEntity
import com.campus.schedule.data.local.entity.PartnerScheduleEntity
import com.campus.schedule.data.local.entity.PeriodTemplateEntity
import com.campus.schedule.data.local.entity.SemesterConfigEntity
import com.campus.schedule.data.local.entity.TypeStyleEntity

/**
 * 应用唯一数据库。所有用户可见数据（课程、节次表、类型外观、学期、设置、伙伴课表）
 * 全部持久化在此，卸载前不丢失；没有服务器，也没有注册登录。
 */
@Database(
    entities = [
        CourseItemEntity::class,
        SemesterConfigEntity::class,
        PeriodTemplateEntity::class,
        TypeStyleEntity::class,
        AppSettingsEntity::class,
        PartnerScheduleEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun courseDao(): CourseDao
    abstract fun configDao(): ConfigDao
    abstract fun partnerDao(): PartnerDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        /** 双检索单例，数据库文件名固定，便于备份/调试。 */
        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "course_schedule.db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}
