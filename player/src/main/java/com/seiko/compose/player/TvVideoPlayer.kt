package com.seiko.compose.player

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.exoplayer2.Player
import com.seiko.compose.player.internal.DefaultVideoPlayerController
import com.seiko.compose.player.ui.MediaControlKeyEvent
import com.seiko.compose.player.ui.MediaControlLayout
import com.seiko.compose.player.ui.MediaPlayerLayout

internal val LocalVideoPlayerController =
  compositionLocalOf<VideoPlayerController> { error("VideoPlayerController is not initialized") }

@Composable
fun rememberPlayer(
  source: VideoPlayerSource,
  factory: VideoPlayerFactory = VideoPlayerFactory
): Player {
  val context = LocalContext.current
  return remember(source, factory) {
    factory.createPlayer(context, source)
  }
}

@Composable
fun rememberVideoPlayerController(player: Player): VideoPlayerController {
  val coroutineScope = rememberCoroutineScope()
  return rememberSaveable(
    player, coroutineScope,
    saver = object : Saver<DefaultVideoPlayerController, VideoPlayerState> {
      override fun restore(value: VideoPlayerState): DefaultVideoPlayerController {
        return DefaultVideoPlayerController(
          player = player,
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
        player = player,
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
  playerFactory: VideoPlayerFactory = VideoPlayerFactory,
) {
  val player = rememberPlayer(source, playerFactory)
  val controller = rememberVideoPlayerController(player)
  TvVideoPlayer(
    player = player,
    controller = controller,
    modifier = modifier,
  )
}

@Composable
fun TvVideoPlayer(
  player: Player,
  controller: VideoPlayerController,
  modifier: Modifier = Modifier,
) {
  CompositionLocalProvider(
    LocalVideoPlayerController provides controller
  ) {
    Box(modifier = modifier) {
      MediaPlayerLayout(player)
      MediaControlLayout(modifier = Modifier.matchParentSize())
      MediaControlKeyEvent(modifier = Modifier.matchParentSize())
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      player.release()
    }
  }
}