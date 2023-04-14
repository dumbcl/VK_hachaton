package ru.ok.android.itmohack2023

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity


class WebViewActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val webView = findViewById<WebView>(R.id.web_view)
        webView.settings.javaScriptEnabled = true
        val htmlText = resources.assets.open("webview.html")
            .bufferedReader()
            .use { it.readText() }
        webView.loadDataWithBaseURL("", htmlText, "text/html", "UTF-8", "")
    }
}