package com.campus.schedule.util

import android.util.Base64
import com.campus.schedule.domain.model.BusySlot
import com.campus.schedule.domain.model.CourseItem
import com.campus.schedule.domain.model.CourseType
import com.campus.schedule.domain.model.WeekParity
import org.json.JSONArray
import org.json.JSONObject

/**
 * 伙伴课表分享码编解码（纯本地，无服务器）。
 * 格式：`CS1:` + Base64(JSON 数组)，JSON 每项为忙碌时段字段。
 */
object ShareCode {

    private const val PREFIX = "CS1:"

    /** 把自己的课表导出为分享码（仅含时间与周次，不含个人备注）。 */
    fun encodeCourses(items: List<CourseItem>): String {
        val arr = JSONArray()
        items.forEach { item ->
            arr.put(
                JSONObject()
                    .put("t", item.title)
                    .put("d", item.dayOfWeek)
                    .put("s", item.startTime)
                    .put("e", item.endTime)
                    .put("ws", item.weekStart)
                    .put("we", item.weekEnd)
                    .put("p", item.weekParity.key)
            )
        }
        val bytes = Base64.encode(arr.toString().toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        return PREFIX + String(bytes, Charsets.UTF_8)
    }

    /** 解析伙伴分享码为忙碌时段列表；格式非法返回 null。 */
    fun decodeBusySlots(code: String): List<BusySlot>? {
        val payload = if (code.startsWith(PREFIX)) code.removePrefix(PREFIX) else code
        return try {
            val json = String(Base64.decode(payload, Base64.NO_WRAP), Charsets.UTF_8)
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                BusySlot(
                    dayOfWeek = o.getInt("d"),
                    startTime = o.getInt("s"),
                    endTime = o.getInt("e"),
                    weekStart = o.optInt("ws", 1),
                    weekEnd = o.optInt("we", 18),
                    weekParity = WeekParity.fromKey(o.optString("p", "ALL"))
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    /** 伙伴忙碌时段序列化为 JSON 存 Room。 */
    fun slotsToJson(slots: List<BusySlot>): String {
        val arr = JSONArray()
        slots.forEach { slot ->
            arr.put(
                JSONObject()
                    .put("d", slot.dayOfWeek)
                    .put("s", slot.startTime)
                    .put("e", slot.endTime)
                    .put("ws", slot.weekStart)
                    .put("we", slot.weekEnd)
                    .put("p", slot.weekParity.key)
            )
        }
        return arr.toString()
    }

    /** 从 JSON 反序列化伙伴忙碌时段。 */
    fun slotsFromJson(json: String): List<BusySlot> = try {
        val arr = JSONArray(json)
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            BusySlot(
                dayOfWeek = o.getInt("d"),
                startTime = o.getInt("s"),
                endTime = o.getInt("e"),
                weekStart = o.optInt("ws", 1),
                weekEnd = o.optInt("we", 18),
                weekParity = WeekParity.fromKey(o.optString("p", "ALL"))
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}
