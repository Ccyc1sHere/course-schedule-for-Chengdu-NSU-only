package com.campus.schedule.data.seed

import com.campus.schedule.data.local.entity.AppSettingsEntity
import com.campus.schedule.data.local.entity.CourseItemEntity
import com.campus.schedule.data.local.entity.PeriodTemplateEntity
import com.campus.schedule.data.local.entity.SemesterConfigEntity
import com.campus.schedule.data.local.entity.TypeStyleEntity
import com.campus.schedule.domain.model.CourseType

/**
 * 首次启动导入的示例数据。
 *
 * 全部内容均为**可编辑数据**：教师字段按用户要求一律留空，由用户自行填写；
 * 节次表 / 类型名 / 颜色 / 学期起始日都能在 App 内修改，也可在设置页清空或重灌。
 */
object SeedData {

    /** 默认节次时间表（第 9–12 节为推断值，全部可在设置页修改）。 */
    val periods: List<PeriodTemplateEntity> = listOf(
        PeriodTemplateEntity(1, "第 1 节", 8 * 60 + 20, 9 * 60),
        PeriodTemplateEntity(2, "第 2 节", 9 * 60 + 10, 9 * 60 + 50),
        PeriodTemplateEntity(3, "第 3 节", 10 * 60, 10 * 60 + 40),
        PeriodTemplateEntity(4, "第 4 节", 10 * 60 + 50, 11 * 60 + 30),
        PeriodTemplateEntity(5, "第 5 节", 11 * 60 + 40, 12 * 60 + 20),
        PeriodTemplateEntity(6, "第 6 节", 14 * 60, 14 * 60 + 40),
        PeriodTemplateEntity(7, "第 7 节", 14 * 60 + 50, 15 * 60 + 30),
        PeriodTemplateEntity(8, "第 8 节", 15 * 60 + 40, 16 * 60 + 20),
        PeriodTemplateEntity(9, "第 9 节", 16 * 60 + 30, 17 * 60 + 10),
        PeriodTemplateEntity(10, "第 10 节", 17 * 60 + 20, 18 * 60),
        PeriodTemplateEntity(11, "第 11 节", 19 * 60, 19 * 60 + 50),
        PeriodTemplateEntity(12, "第 12 节", 20 * 60, 20 * 60 + 50)
    )

    /** 内置 6 类外观默认值；displayName 留空表示使用 App 内置三语文案。 */
    val typeStyles: List<TypeStyleEntity> = listOf(
        TypeStyleEntity(CourseType.COURSE.key, "", "BOOK", 0xFF2C6BE5, 0),
        TypeStyleEntity(CourseType.MEETING.key, "", "GROUP", 0xFF8E5AE8, 1),
        TypeStyleEntity(CourseType.SPORT.key, "", "FITNESS", 0xFF2FA96B, 2),
        TypeStyleEntity(CourseType.MEAL.key, "", "RESTAURANT", 0xFFE8862C, 3),
        TypeStyleEntity(CourseType.ATTENDANCE.key, "", "CHECK", 0xFF2CA8B8, 4),
        TypeStyleEntity(CourseType.OTHER.key, "", "LABEL", 0xFF6B7280, 5)
    )

    /** 学期默认值：2026-08-31（周一）开学，共 18 周，可在设置页修改。 */
    val semester = SemesterConfigEntity(
        startDateEpochDay = java.time.LocalDate.of(2026, 8, 31).toEpochDay(),
        totalWeeks = 18
    )

    val settings = AppSettingsEntity(id = 1)

    /**
     * 示例课表（依据用户提供的便签 + 「23-M-黄敬豪」小程序截图整合）。
     * 教师一律留空；同一门课允许拆成多条不同周次记录（如人工智能通识 I 跳过第 9 周）。
     */
    val courses: List<CourseItemEntity> = listOf(
        // ---------- 周一 ----------
        course("大学英语（一）", 1, 600, 690, "E5108", 1, 16),
        course("程序设计基础（C语言）", 1, 840, 930, "E5221", 1, 16),
        course("多媒体维度", 1, 940, 1040, "A4-201", 1, 16, type = CourseType.OTHER),
        course("劳动教育与实践（1）", 1, 940, 1030, "E5309", 13, 16),
        course("形势与政策（一）", 1, 990, 1080, "A7118", 9, 12),
        // ---------- 周二 ----------
        course("高等数学II（一）", 2, 600, 690, "A6204", 1, 16),
        course("专业导引与职业规划（计算机科学与技术）", 2, 840, 930, "A5302", 9, 16),
        course("晚自习", 2, 1140, 1260, "A6-211", 1, 16, type = CourseType.OTHER),
        // ---------- 周三 ----------
        course("思维创新与开发", 3, 550, 640, "A5203", 1, 16),
        course("人工智能通识I（第1-8周）", 3, 650, 740, "A5201", 1, 8),
        course("人工智能通识I（第10-16周）", 3, 650, 740, "A5201", 10, 16),
        course("大学英语（一）Q07班", 3, 840, 980, "E5111", 1, 16),
        course("国家安全教育（一）", 3, 990, 1080, "", 6, 12, parity = "EVEN"),
        // ---------- 周四 ----------
        course("待确认课程（A4109）", 4, 480, 590, "A4109", 10, 10, type = CourseType.OTHER),
        course("高等数学II（一）", 4, 600, 690, "A7303", 1, 16),
        course("体育与健康（一）", 4, 840, 930, "体育馆", 1, 16, type = CourseType.SPORT),
        course("晚自习", 4, 1140, 1260, "A6-211", 1, 16, type = CourseType.OTHER),
        // ---------- 周五 ----------
        course("程序设计基础（C语言）", 5, 480, 590, "E5128", 1, 16),
        course("思想道德与法治", 5, 600, 740, "E5128", 1, 16),
        course("多媒体维度", 5, 840, 930, "A4-201", 1, 16, type = CourseType.OTHER),
        // ---------- 周六 ----------
        course("晚自习", 6, 1140, 1260, "", 1, 16, type = CourseType.OTHER)
    )

    private fun course(
        title: String,
        dayOfWeek: Int,
        start: Int,
        end: Int,
        location: String,
        weekStart: Int,
        weekEnd: Int,
        type: CourseType = CourseType.COURSE,
        parity: String = "ALL"
    ) = CourseItemEntity(
        title = title,
        typeKey = type.key,
        dayOfWeek = dayOfWeek,
        startTime = start,
        endTime = end,
        location = location,
        teacher = "",
        note = "",
        weekStart = weekStart,
        weekEnd = weekEnd,
        weekParity = parity,
        colorArgb = null
    )
}
