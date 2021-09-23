package com.seiko.compose.player

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.RawResourceDataSource

interface VideoPlayerFactory {
  fun createPlayer(context: Context, source: VideoPlayerSource): Player

  companion object : VideoPlayerFactory {
    override fun createPlayer(context: Context, source: VideoPlayerSource): Player {
      return SimpleExoPlayer.Builder(context)
        .build()
        .apply {
          setMediaSource(source.toMediaSource(context))
          prepare()
        }
    }
  }
}

fun VideoPlayerSource.toMediaSource(context: Context): MediaSource {
  val dataSourceFactory = DefaultDataSourceFactory(context)
  return when {
    this is VideoPlayerSource.Network && url.endsWith("m3u8") -> {
      HlsMediaSource.Factory(dataSourceFactory)
        .createMediaSource(toMediaItem())
    }
    else -> {
      ProgressiveMediaSource.Factory(dataSourceFactory)
        .createMediaSource(toMediaItem())
    }
  }
}

fun VideoPlayerSource.toMediaItem(): MediaItem {
  return when (this) {
    is VideoPlayerSource.Network -> {
      MediaItem.fromUri(Uri.parse(url))
    }
    is VideoPlayerSource.Raw -> {
      MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(resId))
    }
  }
}

