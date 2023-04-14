package ru.ok.android.itmohack2023

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.net.URL

class UrlConnectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cat_facts_layout)
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
        }
    }
}