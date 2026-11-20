package com.campus.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.campus.schedule.data.local.entity.PartnerScheduleEntity
import kotlinx.coroutines.flow.Flow

/** 伙伴课表 DAO：本地单条记录，重复导入即覆盖。 */
@Dao
interface PartnerDao {

    @Query("SELECT * FROM partner_schedule WHERE id = 1")
    fun observePartner(): Flow<PartnerScheduleEntity?>

    @Query("SELECT * FROM partner_schedule WHERE id = 1")
    suspend fun getPartner(): PartnerScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPartner(partner: PartnerScheduleEntity)

    @Query("DELETE FROM partner_schedule WHERE id = 1")
    suspend fun clearPartner()
}
