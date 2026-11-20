package com.campus.schedule.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 伙伴课表，只存一份（可重复导入覆盖）。
 * [slotsJson] 是本地序列化后的忙碌时段，不上传服务器。
 */
@Entity(tableName = "partner_schedule")
data class PartnerScheduleEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val slotsJson: String,
    val importedAt: Long
)
