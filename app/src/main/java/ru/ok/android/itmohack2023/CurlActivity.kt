package ru.ok.android.itmohack2023

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class CurlActivity : AppCompatActivity() {

    private lateinit var factsList: ViewGroup
    private lateinit var messageTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_curl)
        messageTv = findViewById(R.id.error_message)
        factsList = findViewById(R.id.facts_list)
        showMessage("Загрузка данных")
        Thread { executeShellScript() }.start()
    }

    private fun executeShellScript() {
        val command = """
                    curl -X GET \
                      https://cat-fact.herokuapp.com/facts
                  """.trimIndent()

        try {
            val process = Runtime.getRuntime().exec(command)
            val response = BufferedReader(InputStreamReader(process.inputStream)).readLine()
            val textJson = JSONArray(response)

            val facts = arrayListOf<String>()
            for (i in 0 until textJson.length()) {
                val factJson = textJson.getJSONObject(i)
                val factText = factJson.getString("text")
                facts.add(factText)
            }
            showFacts(facts)
        } catch (e: Exception) {
            showMessage("Не удалось выполнить команду [$command].")
        }
    }

    private fun showFacts(facts: List<String>) {
        runOnUiThread {
            messageTv.visibility = View.GONE
            val bottomPadding = resources.getDimensionPixelOffset(R.dimen.padding_normal)
            facts.forEach { fact ->
                val textView = TextView(this).apply {
                    text = fact
                    setPadding(bottomPadding)
                }
                factsList.addView(textView)
            }
        }
    }

    private fun showMessage(error: String) {
        messageTv.visibility = View.VISIBLE
        messageTv.text = error
    }
}