package ru.ok.android.itmohack2023

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.ok.android.itmohack2023.retrofit.CatsApi
import ru.ok.android.itmohack2023.retrofit.RetrofitProvider
import ru.ok.android.itmohack2023.retrofit.dto.CatFact

class RetrofitActivity : Activity() {

    private val compositeDisposable = CompositeDisposable()

    private val catsApi: CatsApi by lazy {
        val retrofit = RetrofitProvider.retrofit
        CatsApi.provideCatApi(retrofit)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit)
        val textView = findViewById<TextView>(R.id.tv_text)

        findViewById<View>(R.id.rx).setOnClickListener {
            val disposable = catsApi.getFactRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        textView.text = response.fact
                    },
                    { error ->
                        handleError(error)
                    }
                )
            compositeDisposable.add(disposable)
        }

        findViewById<View>(R.id.callback).setOnClickListener {
            catsApi.getFactCall().enqueue(object : Callback<CatFact> {
                override fun onResponse(call: Call<CatFact>, response: Response<CatFact>) {
                    if (response.isSuccessful) {
                        textView.text = response.body()?.fact
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "error fetching data ${response.errorBody()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<CatFact>, t: Throwable) {
                    handleError(t)
                }
            })
        }

        findViewById<View>(R.id.blocked).setOnClickListener {
            Threads.ioPool.execute {
                try {
                    val response = catsApi.getFactCall().execute()
                    runOnUiThread {
                        textView.text = response.body()?.fact
                    }
                } catch (t: Throwable) {
                    runOnUiThread {
                        handleError(t)
                    }
                }
            }
        }
    }

    private fun handleError(t: Throwable) {
        Toast.makeText(
            applicationContext,
            "error fetching data ${t.localizedMessage}",
            Toast.LENGTH_SHORT
        )
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}