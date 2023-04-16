package ru.ok.android.itmohack2023

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import ru.ok.android.networktracker.NetworkType
import ru.ok.android.networktracker.Tracker
import ru.ok.android.networktracker.TrackerEvent
import java.io.IOException

class OkHttpActivity : AppCompatActivity() {
    private val context: Context = this
    val tracker: Tracker = Tracker(context, NetworkType.BASIC, 1000L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ok_http)

        tracker.subscribe("OkHttpActivity", TrackerEvent.CHANGE_NETWORK, {print("hello")})

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

    override fun onDestroy() {
        super.onDestroy()
        tracker.clean()
    }

    @Throws(IOException::class)
    fun run(url: String): String? {
        val request: Request = Request.Builder()
            .url(url)
            .build()
        OkHttpClient().newCall(request).execute().use { response -> return response.body?.string() }
    }
}