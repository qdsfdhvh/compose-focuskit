package com.seiko.compose.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.seiko.compose.player.internal.DefaultVlcVideoPlayerController
import com.seiko.compose.player.ui.MediaControlKeyEvent
import com.seiko.compose.player.ui.MediaControlLayout
import com.seiko.compose.player.ui.VlcMediaPlayerLayout
import org.videolan.libvlc.MediaPlayer

@Composable
fun rememberVlcVideoPlayerController(player: MediaPlayer): VideoPlayerController {
  val coroutineScope = rememberCoroutineScope()
  return rememberSaveable(
    player, coroutineScope,
    saver = object : Saver<DefaultVlcVideoPlayerController, VideoPlayerState> {
      override fun restore(value: VideoPlayerState): DefaultVlcVideoPlayerController {
        return DefaultVlcVideoPlayerController(
          player = player,
          initialState = value,
          coroutineScope = coroutineScope
        )
      }

      override fun SaverScope.save(value: DefaultVlcVideoPlayerController): VideoPlayerState {
        return value.currentState
      }
    },
    init = {
      DefaultVlcVideoPlayerController(
        player = player,
        initialState = VideoPlayerState(),
        coroutineScope = coroutineScope
      )
    }
  )
}

@Composable
fun rememberVlcPlayer(
  source: VideoPlayerSource,
  factory: VlcVideoPlayerFactory = VlcVideoPlayerFactory
): MediaPlayer {
  val context = LocalContext.current
  return remember(source, factory) {
    factory.createPlayer(context, source)
  }
}

@Composable
fun TvVlcVideoPlayer(
  source: VideoPlayerSource,
  modifier: Modifier = Modifier,
  playerFactory: VlcVideoPlayerFactory = VlcVideoPlayerFactory,
) {
  val mediaPlayer = rememberVlcPlayer(source, playerFactory)
  val controller = rememberVlcVideoPlayerController(mediaPlayer)
  TvVlcVideoPlayer(
    mediaPlayer = mediaPlayer,
    controller = controller,
    modifier = modifier,
  )
}

@Composable
fun TvVlcVideoPlayer(
  mediaPlayer: MediaPlayer,
  controller: VideoPlayerController,
  modifier: Modifier = Modifier,
) {
  CompositionLocalProvider(
    LocalVideoPlayerController provides controller
  ) {
    Box(modifier = modifier.background(Color.Black)) {
      VlcMediaPlayerLayout(mediaPlayer, modifier = Modifier.matchParentSize())
      MediaControlLayout(modifier = Modifier.matchParentSize())
      MediaControlKeyEvent(modifier = Modifier.matchParentSize())
    }
  }
}