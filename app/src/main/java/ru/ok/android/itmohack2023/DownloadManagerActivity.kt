package ru.ok.android.itmohack2023

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DownloadManagerActivity : AppCompatActivity() {
    private lateinit var catButton: Button
    private lateinit var dogButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloadmanager)

        catButton = findViewById(R.id.cat_button)
        dogButton = findViewById(R.id.dog_button)

        dogButton.setOnClickListener {
            download(DOGS[(0 until dogSize).random()], "dog")
        }
        catButton.setOnClickListener {
            download(CATS[(0 until catSize).random()], "cat")
        }
    }

    private fun download(uriString: String, type: String) {
        val downloadmanager: DownloadManager =
            getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri: Uri = Uri.parse(uriString)
        val request: DownloadManager.Request = DownloadManager.Request(uri)
        request.setTitle("Random $type")
        request.setDescription("Downloading")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            uri.lastPathSegment
        )
        downloadmanager.enqueue(request)
    }

    companion object {
        val CATS = listOf(
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
        val DOGS = listOf(
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
        val catSize = CATS.size
        val dogSize = DOGS.size
    }
}