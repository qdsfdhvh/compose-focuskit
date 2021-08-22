package com.seiko.compose.player

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.seiko.compose.focuskit.TvControllerKey
import com.seiko.compose.focuskit.onTvKeyEvent
import com.seiko.compose.player.internal.DefaultVideoPlayerController
import com.seiko.compose.player.ui.ExoPlayerView
import com.seiko.compose.player.ui.MediaControlKeyEvent
import com.seiko.compose.player.ui.MediaControlLayout
import com.seiko.compose.player.ui.PlayerSurface

internal val LocalVideoPlayerController =
  compositionLocalOf<VideoPlayerController> { error("VideoPlayerController is not initialized") }

@Composable
fun rememberVideoPlayerController(exoPlayer: ExoPlayer): VideoPlayerController {
  val coroutineScope = rememberCoroutineScope()

  return rememberSaveable(
    exoPlayer, coroutineScope,
    saver = object : Saver<DefaultVideoPlayerController, VideoPlayerState> {
      override fun restore(value: VideoPlayerState): DefaultVideoPlayerController {
        return DefaultVideoPlayerController(
          exoPlayer = exoPlayer,
          initialState = value,
          coroutineScope = coroutineScope
        )
      }

      override fun SaverScope.save(value: DefaultVideoPlayerController): VideoPlayerState {
        return value.currentState
      }
    },
    init = {
      DefaultVideoPlayerController(
        exoPlayer = exoPlayer,
        initialState = VideoPlayerState(),
        coroutineScope = coroutineScope
      )
    }
  )
}

@Composable
fun TvVideoPlayer(
  source: VideoPlayerSource,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current

  val exoPlayer = remember(source) {
    SimpleExoPlayer.Builder(context)
      .build()
      .apply {
        val dataSourceFactory = DefaultDataSourceFactory(context)

        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
          .createMediaSource(source.toMediaItem())

        setMediaSource(mediaSource)
        prepare()
      }
  }

  val controller = rememberVideoPlayerController(exoPlayer)
  CompositionLocalProvider(
    LocalVideoPlayerController provides controller
  ) {
    Box {
      ExoPlayerView(exoPlayer)
      MediaControlLayout(modifier = Modifier.matchParentSize())
      MediaControlKeyEvent(modifier = Modifier.matchParentSize())
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      exoPlayer.release()
    }
  }
}