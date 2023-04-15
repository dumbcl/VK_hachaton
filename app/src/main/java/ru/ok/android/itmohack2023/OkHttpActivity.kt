package ru.ok.android.itmohack2023

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException

class OkHttpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ok_http)
        val list = findViewById<ViewGroup>(R.id.list)

        Threads.ioPool.execute {
            run("https://cat-fact.herokuapp.com/facts")?.let {

                val textJson = JSONArray(it)
                for (i in 0 until textJson.length()) {
                    val factJson = textJson.getJSONObject(i)
                    val factText = factJson.getString("text")

                    runOnUiThread {
                        val textView = TextView(this)
                        textView.text = factText
                        list.addView(textView)
                        val space = Space(this)
                        space.minimumHeight =
                            resources.getDimensionPixelOffset(R.dimen.padding_normal)
                        list.addView(space)
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    fun run(url: String): String? {
        val request: Request = Request.Builder()
            .url(url)
            .build()
        OkHttpClient().newCall(request).execute().use { response -> return response.body?.string() }
    }
}