# Room：保留 Entity / DAO 的无参构造与表名
-keep class com.campus.schedule.data.local.entity.** { *; }
-keep class com.campus.schedule.domain.model.** { *; }

# Glance 小组件与 BroadcastReceiver 由 Manifest 反射创建
-keep class com.campus.schedule.widget.** { *; }
-keep class com.campus.schedule.notification.** { *; }

# kotlinx.serialization / Gson 未引入；分享码使用 org.json，无需额外规则
-dontwarn org.slf4j.**
