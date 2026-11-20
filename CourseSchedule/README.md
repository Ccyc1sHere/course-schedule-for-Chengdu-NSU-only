# CourseSchedule 课程表

面向大学生的本地课程表 App。Kotlin + Jetpack Compose + Material 3，最低 Android 10（API 29），目标 SDK 35。**纯本地存储（Room），不注册、不联网、无服务器**。

## 功能

- **自动日期 / 时间感知**：打开定位到今天；顶部常驻「距下一节课 X 分钟」实时倒计时；无课/已结束有对应文案。
- **完整 CRUD**：标题、类型、星期、起止时间、地点、教师、备注、周次范围、单双周、颜色；类型（课程/会议/运动/餐饮/出勤/其他）可自定义名称/图标/配色；拖动排序；一键复制某天课表到另一天。
- **日 / 周双视图**：周视图 7 列网格 + 节次时间轴，重叠日程自动并排；日视图时间轴，突出「正在进行 / 即将开始」；切换保持定位日期不变。
- **周次与单双周引擎**：学期起始日可设，自动算第几周；每条日程独立周次范围与单双周；非本周日程可降级显示（低透明度 + 「非本周」角标）/ 隐藏 / 灰色占位；「回到本周」快捷按钮。
- **外观**：浅色 / 深色 / 跟随系统，300ms 全局颜色过渡；Android 12+ 可选动态取色，10–11 恒用品牌配色；Edge-to-Edge 状态栏 / 导航栏图标随主题切换。
- **三语**：简体中文 / 繁體中文 / English，应用内切换（per-app language）。
- **提醒与勿扰**：课前 X 分钟本地通知（13+ 运行时申请 `POST_NOTIFICATIONS`，拒绝后不反复弹窗）；可选上课自动勿扰（需手动授权）。
- **桌面小组件**：2×2「今日课程」Glance 小组件，显示下一节课 + 倒计时。
- **伙伴课表**：导出/导入分享码（本地 Base64+JSON），周视图半透明叠加对方忙碌时段。
- **示例课表**：首次启动导入一份示例课表（教师留空），可一键清空 / 重灌。

## 技术栈

Kotlin · Compose · Material 3 · MVVM + Repository + 单一数据源（Room）· Coroutines + Flow · Navigation Compose · Glance。

## 项目结构

```
app/src/main/java/com/campus/schedule/
├── ScheduleApp.kt / MainActivity.kt        # Application + 单 Activity 宿主
├── data/
│   ├── local/                              # Room：Entity / DAO / Database / Mappers
│   ├── repository/ScheduleRepository.kt    # 单一数据源
│   └── seed/SeedData.kt                    # 首次启动示例课表
├── domain/
│   ├── model/                              # 领域模型
│   └── logic/                              # 纯函数：周次 / 冲突 / 倒计时
├── ui/
│   ├── theme/  navigation/  components/
│   ├── schedule/  editor/  settings/
├── notification/                           # 课前提醒 + 勿扰 + 开机重排
├── widget/                                 # Glance 小组件
└── util/ShareCode.kt                       # 伙伴课表分享码
```

## 构建与打包 APK

前置：JDK 17 + Android SDK（`ANDROID_HOME` 已配置，或 Android Studio 自带）。

```bash
cd CourseSchedule

# 1) 生成 Gradle Wrapper（首次；已安装 Gradle 可跳过直接 ./gradlew）
gradle wrapper --gradle-version 8.9

# 2) 编译 debug APK（直接用 debug 签名，可立即安装）
./gradlew :app:assembleDebug
# Windows PowerShell:
./gradlew.bat :app:assembleDebug

# 3) 编译 release APK（minify + 资源压缩，签名暂用 debug 方便安装）
./gradlew :app:assembleRelease
```

产物路径：
- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/outputs/apk/release/app-release.apk`

安装（真机开启「未知来源」）：

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

正式发布：在 `app/build.gradle.kts` 的 `release` 块替换为自己的 `signingConfig`，并生成 keystore。

## 单元测试

```bash
./gradlew :app:testDebugUnitTest
```

覆盖：周次计算、冲突检测、倒计时计算（纯函数，无需真机）。

## 真机验证边界场景清单

1. **倒计时**：有课 / 正在进行 / 已全部结束 / 今日无课四种状态文案正确，跨分钟自动刷新。
2. **周次引擎**：修改学期起始日，观察「第几周」跳变；第 1 周前 / 18 周后显示「开学前 / 学期已结束」。
3. **单双周**：设一条「仅单周」课程，切到双周确认不显示（或降级 + 「非本周」角标）。
4. **非本周策略**：DIM / HIDE / PLACEHOLDER 三档切换生效。
5. **周视图翻页**：左右滑动切周流畅；重叠课程（周一 15:40 两条）并排显示不遮挡。
6. **删除动画**：先收缩淡出，再提交删除，列表无闪跳。
7. **复制日**：复制周一全部日程到周日，数量提示正确。
8. **编辑返回**：修改后返回弹确认对话框；直接新建未填返回不弹。
9. **主题**：浅/深/跟随系统切换，300ms 渐变；深色下无白色闪块（日期/时间选择器、对话框）。
10. **三语**：切换语言，界面即时切换且重启保留；课程名（用户数据）不翻译。
11. **通知**：Android 13+ 首次开提醒申请权限；拒绝后不再弹，设置页有入口；关闭提醒后闹钟取消。
12. **勿扰**：开勿扰引导授权，拒绝不反复弹。
13. **小组件**：添加到桌面，显示下一节课与倒计时，随系统深浅主题。
14. **伙伴课表**：导出分享码 → 换设备导入 → 周视图半透明叠加忙碌时段。
15. **数据持久化**：杀掉进程 / 重启，课表与设置不丢。
16. **Android 10 (API 29)**：确认无动态取色、无崩溃、品牌配色一致。

## 已知限制

- Android 14+（targetSdk 35）系统默认拒绝 `SCHEDULE_EXACT_ALARM`，首次开启提醒需在系统「闹钟和提醒」里允许精确闹钟；已做降级（不影响 App 主体功能）。
