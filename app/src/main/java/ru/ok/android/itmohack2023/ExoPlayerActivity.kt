package ru.ok.android.itmohack2023

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import ru.ok.android.networktracker.Email
import ru.ok.android.networktracker.NetworkType
import ru.ok.android.networktracker.Tracker
import ru.ok.android.networktracker.TrackerEvent

class ExoPlayerActivity : AppCompatActivity() {
    private val context: Context = this
    //val email: Email = Email("smtp.gmail.com", "587", "senrec17@gmail.com", "pass123word", "kolahola49@gmail.com", "Logs", "This is your logs", null)
    val tracker: Tracker = Tracker(context, NetworkType.BASIC, 1000L)

    private var exoPlayer: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exo_player)

        tracker.subscribe("ExoPlayerActivity", TrackerEvent.CHANGE_NETWORK, {print("hello")})
        //tracker.subscribe("UrlConnectionActivity", TrackerEvent.EMAIL, { print("hi") })
        preparePlayer()
    }

    private fun preparePlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer?.playWhenReady = true
        val playerView = findViewById<StyledPlayerView>(R.id.playerView)
        playerView.player = exoPlayer
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaItem = MediaItem.fromUri(URL)
        val mediaSource = HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
        exoPlayer?.apply {
            setMediaSource(mediaSource)
            seekTo(playbackPosition)
            playWhenReady = playWhenReady
            prepare()
        }
    }

    private fun releasePlayer() {
        exoPlayer?.let { player ->
            playbackPosition = player.currentPosition
            playWhenReady = player.playWhenReady
            player.release()
            exoPlayer = null
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }


    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        tracker.clean()
    }

    companion object {
        const val URL = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"
    }
}