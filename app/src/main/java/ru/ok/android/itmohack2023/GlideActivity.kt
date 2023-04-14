package ru.ok.android.itmohack2023

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class GlideActivity : AppCompatActivity() {
    private lateinit var cat1: ImageView
    private lateinit var cat2: ImageView
    private lateinit var cat3: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glide)

        cat1 = findViewById(R.id.cat_photo_1)
        cat2 = findViewById(R.id.cat_photo_2)
        cat3 = findViewById(R.id.cat_photo_3)

        bindImages()
    }

    override fun onStart() {
        super.onStart()
        bindImages()
    }

    private fun bindImages() {
        Glide.with(this).load(URLS[(0 until size).random()]).into(cat1)
        Glide.with(this).load(URLS[(0 until size).random()]).into(cat2)
        Glide.with(this).load(URLS[(0 until size).random()]).into(cat3)
    }

    companion object {
        val URLS = listOf(
            "https://cdn2.thecatapi.com/images/cm7.jpg",
            "https://cdn2.thecatapi.com/images/2iq.jpg",
            "https://cdn2.thecatapi.com/images/4m9.jpg",
            "https://cdn2.thecatapi.com/images/MuEGe1-Sz.jpg",
            "https://cdn2.thecatapi.com/images/MTk2NjI0Mw.jpg",

            "https://cdn2.thecatapi.com/images/4im.gif",
            "https://cdn2.thecatapi.com/images/in-CD5LH5.jpg",
            "https://cdn2.thecatapi.com/images/xnzzM6MBI.jpg",
            "https://cdn2.thecatapi.com/images/XAvnPwmqZ.jpg",
            "https://cdn2.thecatapi.com/images/MTg2NTExMw.jpg",

            "https://cdn2.thecatapi.com/images/MTczMjgzOQ.jpg",
            "https://cdn2.thecatapi.com/images/MTY4MjUxMA.png",
            "https://cdn2.thecatapi.com/images/ds4.jpg",
            "https://cdn2.thecatapi.com/images/de0.jpg",
            "https://cdn2.thecatapi.com/images/ag9.jpg"
        )
        val size = PicassoActivity.URLS.size
    }
}



