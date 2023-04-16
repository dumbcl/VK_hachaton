package ru.ok.android.itmohack2023

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import ru.ok.android.networktracker.NetworkType
import ru.ok.android.networktracker.Tracker
import ru.ok.android.networktracker.TrackerEvent

class PicassoActivity : AppCompatActivity() {
    private val context: Context = this
    val tracker: Tracker = Tracker(context, NetworkType.BASIC, 1000L)
    private lateinit var dog1: ImageView
    private lateinit var dog2: ImageView
    private lateinit var dog3: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picasso)

        tracker.subscribe("OkHttpActivity", TrackerEvent.CHANGE_NETWORK, {print("hello")})

        dog1 = findViewById(R.id.dog_photo_1)
        dog2 = findViewById(R.id.dog_photo_2)
        dog3 = findViewById(R.id.dog_photo_3)

        bindImages()

        tracker.subscribe("PicassoActivity", TrackerEvent.CHANGE_NETWORK, {print("hello")})
    }

    override fun onStart() {
        super.onStart()
        bindImages()
    }

    private fun bindImages() {

        Picasso.get().load(URLS[(0 until size).random()]).placeholder(getDrawable(R.drawable.ico_dog)!!).into(dog1)
        Picasso.get().load(URLS[(0 until size).random()]).placeholder(getDrawable(R.drawable.ico_dog)!!).into(dog2)
        Picasso.get().load(URLS[(0 until size).random()]).placeholder(getDrawable(R.drawable.ico_dog)!!).into(dog3)
    }

    companion object {
        val URLS = listOf(
            "https://cdn2.thedogapi.com/images/fqYLZ9MFz.jpg",
            "https://cdn2.thedogapi.com/images/omhpknDX6.jpg",
            "https://cdn2.thedogapi.com/images/Hk53_dnSQ_1280.jpg",
            "https://cdn2.thedogapi.com/images/ooXgHah90.jpg",
            "https://cdn2.thedogapi.com/images/SycZKu2Sm_1280.jpg",

            "https://cdn2.thedogapi.com/images/SkvZgx94m_1280.jpg",
            "https://cdn2.thedogapi.com/images/bhF8zbrRq.jpg",
            "https://cdn2.thedogapi.com/images/FQpFX5-UR.jpg",
            "https://cdn2.thedogapi.com/images/eoHqcDbsV.jpg",
            "https://cdn2.thedogapi.com/images/iYNlO0y4T.jpg",

            "https://cdn2.thedogapi.com/images/1tLAP4Eo4.jpg",
            "https://cdn2.thedogapi.com/images/sHPfcmVsV.jpg",
            "https://cdn2.thedogapi.com/images/6f5n_42mB.jpg",
            "https://cdn2.thedogapi.com/images/AbmDht85L.jpg",
            "https://cdn2.thedogapi.com/images/VDRwpgVTB.jpg"
        )
        val size = URLS.size
    }

    override fun onDestroy() {
        super.onDestroy()
        tracker.clean()
    }
}




