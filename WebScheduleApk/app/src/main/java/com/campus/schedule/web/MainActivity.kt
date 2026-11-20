package com.campus.schedule.web

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {

    private lateinit var root: View
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 内容延伸到系统栏后面，由下方 inset 控制 WebView 实际可视区域
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        root = findViewById(R.id.root)
        webView = findViewById(R.id.webView)
        webView.settings.apply {
            // 课程表为纯前端实现，必须开启 JS 与本地存储
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            // 资产以 file:///android_asset 加载，允许同源文件访问
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            // 适配移动端视口，禁止缩放以保证布局与原设计一致
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(false)
            builtInZoomControls = false
            displayZoomControls = false
            textZoom = 100
        }
        // 所有跳转仍在本 WebView 内完成，不唤起外部浏览器
        webView.webViewClient = WebViewClient()
        webView.setBackgroundColor(LIGHT_BG)

        // 网页切换深浅色时，同步系统状态栏 / 导航栏的图标明暗
        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun setDarkMode(dark: Boolean) {
                runOnUiThread { applySystemBars(dark) }
            }
        }, "AndroidTheme")

        // edge-to-edge 下把系统栏高度作为根布局内边距：
        // 整体内容（WebView）统一下移，适配状态栏、导航栏与刘海高度
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            view.setPadding(
                maxOf(bars.left, cutout.left),
                maxOf(bars.top, cutout.top),
                maxOf(bars.right, cutout.right),
                maxOf(bars.bottom, cutout.bottom)
            )
            insets
        }
        ViewCompat.requestApplyInsets(root)

        // 首屏默认浅色；网页加载后会通过 AndroidTheme 再校正一次
        applySystemBars(false)
        webView.loadUrl("file:///android_asset/index.html")

        // 返回键优先交给网页：关闭弹窗 / 返回主界面 / 退出编辑模式；
        // 网页未消费时才回退 WebView 历史或退出应用。
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                webView.evaluateJavascript(
                    "(function(){try{return !!(window.handleAndroidBack&&window.handleAndroidBack())}catch(e){return false}})()"
                ) { result ->
                    val handled = result?.trim()?.trim('"') == "true"
                    if (!handled) {
                        if (webView.canGoBack()) {
                            webView.goBack()
                        } else {
                            isEnabled = false
                            onBackPressedDispatcher.onBackPressed()
                        }
                    }
                }
            }
        })
    }

    @Suppress("DEPRECATION")
    private fun applySystemBars(dark: Boolean) {
        val bg = if (dark) DARK_BG else LIGHT_BG
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        // API 24/25 不支持深色导航栏图标，保留黑色导航栏避免图标看不清
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            window.navigationBarColor = Color.BLACK
        }
        window.decorView.setBackgroundColor(bg)
        root.setBackgroundColor(bg)
        webView.setBackgroundColor(bg)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = !dark
            isAppearanceLightNavigationBars = !dark
        }
    }

    companion object {
        private val LIGHT_BG = Color.rgb(0xF6, 0xF7, 0xFB)
        private val DARK_BG = Color.rgb(0x1B, 0x1E, 0x22)
    }
}
