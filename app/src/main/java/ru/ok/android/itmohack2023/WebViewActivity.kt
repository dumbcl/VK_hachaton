package ru.ok.android.itmohack2023

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import ru.ok.android.networktracker.NetworkType
import ru.ok.android.networktracker.Tracker
import ru.ok.android.networktracker.TrackerEvent


class WebViewActivity : AppCompatActivity() {
    private val context: Context = this
    val tracker: Tracker = Tracker(context, NetworkType.BASIC, 1000L)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        tracker.subscribe("WebViewActivity", TrackerEvent.CHANGE_NETWORK, {print("hello")})

        val webView = findViewById<WebView>(R.id.web_view)
        webView.settings.javaScriptEnabled = true
        val htmlText = resources.assets.open("webview.html")
            .bufferedReader()
            .use { it.readText() }
        webView.loadDataWithBaseURL("", htmlText, "text/html", "UTF-8", "")


    }

    override fun onDestroy() {
        super.onDestroy()
        tracker.clean()
    }
}