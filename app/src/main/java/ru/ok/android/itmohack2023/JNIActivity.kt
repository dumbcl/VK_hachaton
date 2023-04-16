package ru.ok.android.itmohack2023

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import ru.ok.android.networktracker.NetworkType
import ru.ok.android.networktracker.Tracker
import ru.ok.android.networktracker.TrackerEvent

class JNIActivity : AppCompatActivity() {
    private val context: Context = this
    val tracker: Tracker = Tracker(context, NetworkType.BASIC, 1000L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jniactivity)

        tracker.subscribe("JNIActivity", TrackerEvent.CHANGE_NETWORK, {print("hello")})

        Threads.ioPool.execute {
            var result = nativeFunction() ?: return@execute
            result = result.dropWhile { it != '{' }

            val textJson = JSONObject(result)
            val act =
                textJson.getString("activity")
            runOnUiThread {
                findViewById<TextView>(R.id.result).text = act
            }
        }
    }

    external fun nativeFunction(): String?

    companion object {
        init {
            System.loadLibrary("jnisocket");
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tracker.clean()
    }
}