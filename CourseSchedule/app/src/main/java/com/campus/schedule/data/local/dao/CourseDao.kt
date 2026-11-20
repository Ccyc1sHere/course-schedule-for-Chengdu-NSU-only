package com.campus.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.campus.schedule.data.local.entity.CourseItemEntity
import kotlinx.coroutines.flow.Flow

/** 日程表 DAO。列表查询全部按 `day_of_week, start_time, sort_order` 排序。 */
@Dao
interface CourseDao {

    @Query("SELECT * FROM course_items ORDER BY day_of_week, start_time, sort_order, id")
    fun observeAll(): Flow<List<CourseItemEntity>>

    @Query("SELECT * FROM course_items WHERE id = :id")
    suspend fun getById(id: Long): CourseItemEntity?

    @Query("SELECT * FROM course_items ORDER BY day_of_week, start_time, sort_order, id")
    suspend fun getAll(): List<CourseItemEntity>

    @Query("SELECT COUNT(*) FROM course_items")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CourseItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CourseItemEntity>)

    @Update
    suspend fun update(item: CourseItemEntity)

    @Transaction
    suspend fun upsert(item: CourseItemEntity) {
        if (item.id == 0L) insert(item) else update(item)
    }

    @Query("DELETE FROM course_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM course_items")
    suspend fun deleteAll()

    /** 拖动排序后整体提交。 */
    @Transaction
    suspend fun updateSortOrders(items: List<CourseItemEntity>) {
        items.forEach { update(it.copy(sortOrder = it.sortOrder)) }
    }

    /** 复制某天的全部日程到另一天（保留类型/周次/颜色，时间不变）。 */
    @Transaction
    suspend fun copyDay(fromDay: Int, toDay: Int) {
        val source = getAll().filter { it.dayOfWeek == fromDay }
        val copied = source.map {
            it.copy(id = 0, dayOfWeek = toDay, createdAt = System.currentTimeMillis())
        }
        copied.forEach { insert(it) }
    }
}
