package com.seiko.compose.player

import android.content.Context
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

interface VideoPlayerFactory {
  fun createPlayer(context: Context, source: VideoPlayerSource): Player

  companion object : VideoPlayerFactory {
    override fun createPlayer(context: Context, source: VideoPlayerSource): Player {
      return SimpleExoPlayer.Builder(context)
        .build()
        .apply {
          val dataSourceFactory = DefaultDataSourceFactory(context)

          val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(source.toMediaItem())

          setMediaSource(mediaSource)
          prepare()
        }
    }
  }
}