package ru.ok.android.itmohack2023

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

class ExoPlayerActivity : AppCompatActivity() {

    private var exoPlayer: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exo_player)
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
    }

    companion object {
        const val URL = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"
    }
}