package ru.ok.android.itmohack2023

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.google.android.material.button.MaterialButton
import ru.ok.android.networktracker.NetworkType
import ru.ok.android.networktracker.Tracker
import ru.ok.android.networktracker.TrackerEvent

class FrescoActivity : AppCompatActivity() {
    private val context: Context = this
    val tracker: Tracker = Tracker(context, NetworkType.BASIC, 1000L)

    private lateinit var animalsView: SimpleDraweeView
    private lateinit var anotherBtn: MaterialButton

    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fresco)

        tracker.subscribe("FrescoActivity", TrackerEvent.CHANGE_NETWORK, {print("hello")})

        animalsView = findViewById(R.id.animals_image)
        anotherBtn = findViewById(R.id.another_btn)

        getAnimal()

        anotherBtn.setOnClickListener {
            getAnimal()
        }

    }

    private fun getAnimal() {
        val animal = SAMPLE_URIS_PNG[position]
        val request = ImageRequestBuilder
            .newBuilderWithSource(Uri.parse(animal))
            .disableDiskCache()
            .disableMemoryCache()
            .build()
        animalsView.setImageRequest(request)
        position = (0..5).random()
    }

    private val SAMPLE_URIS_PNG =
        arrayOf(
            "https://frescolib.org/static/sample-images/animal_a.png",
            "https://frescolib.org/static/sample-images/animal_b.png",
            "https://frescolib.org/static/sample-images/animal_c.png",
            "https://frescolib.org/static/sample-images/animal_e.png",
            "https://frescolib.org/static/sample-images/animal_f.png",
            "https://frescolib.org/static/sample-images/animal_g.png"
        )

    override fun onDestroy() {
        super.onDestroy()
        tracker.clean()
    }
}
