package ru.ok.android.itmohack2023

import android.content.Context
import android.net.TrafficStats
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import ru.ok.android.networktracker.Email
import ru.ok.android.networktracker.NetworkType
import java.net.URL
import ru.ok.android.networktracker.Tracker
import ru.ok.android.networktracker.TrackerEvent

class UrlConnectionActivity : AppCompatActivity() {
    private val context: Context = this

    //val email: Email = Email("smtp.gmail.com", "587", "senrec17@gmail.com", "pass123word", "kolahola49@gmail.com", "Logs", "This is your logs", null)
    //val tracker: Tracker = Tracker(context, NetworkType.BASIC, 1000L, emailOpts = email)
    val tracker: Tracker = Tracker(context, NetworkType.BASIC, 1000L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cat_facts_layout)
        tracker.subscribe("UrlConnectionActivity", TrackerEvent.CHANGE_NETWORK, { print("hello") })
        //tracker.subscribe("UrlConnectionActivity", TrackerEvent.EMAIL, { print("hi") })
        val list = findViewById<ViewGroup>(R.id.list)


        Threads.ioPool.execute {
            val connection = URL("https://cat-fact.herokuapp.com/facts").openConnection()
            val text = connection.getInputStream().bufferedReader().readText()
            val textJson = JSONArray(text)
            for (i in 0 until textJson.length()) {
                val factJson = textJson.getJSONObject(i)
                val factText = factJson.getString("text")

                runOnUiThread {
                    val textView = TextView(this)
                    textView.text = factText
                    list.addView(textView)
                    val space = Space(this)
                    space.minimumHeight = resources.getDimensionPixelOffset(R.dimen.padding_normal)
                    list.addView(space)
                }
            }
            val tt = findViewById<TextView>(R.id.textView)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        tracker.clean()
    }

}