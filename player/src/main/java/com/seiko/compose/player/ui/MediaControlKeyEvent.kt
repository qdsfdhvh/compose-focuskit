package com.seiko.compose.player.ui

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.seiko.compose.focuskit.TvControllerKey
import com.seiko.compose.focuskit.onTvKeyEvent
import com.seiko.compose.player.LocalVideoPlayerController
import com.seiko.compose.player.VideoSeekDirection

@Composable
fun MediaControlKeyEvent(modifier: Modifier = Modifier) {
  val controller = LocalVideoPlayerController.current
  val state by controller.state.collectAsState()

  Box(
    modifier = modifier
      .onTvKeyEvent {
        when (it) {
          TvControllerKey.Enter -> {
            if (state.isPlaying) {
              controller.pause()
              controller.showControl()
            } else {
              controller.play()
              controller.hideControl()
            }
            true
          }
          TvControllerKey.Down -> {
            if (state.controlsVisible) {
              controller.hideControl()
            } else {
              controller.showControl()
            }
            true
          }
          TvControllerKey.Left -> {
            controller.seekRewind()
            true
          }
          TvControllerKey.Right -> {
            controller.seekForward()
            true
          }
          else -> false
        }
      }
      .focusable(),
  ) {
    VideoSeekAnimation(
      modifier = Modifier.matchParentSize(),
      seekDirection = state.seekDirection,
    )
  }
}

@Composable
fun VideoSeekAnimation(
  seekDirection: VideoSeekDirection,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    when (seekDirection) {
      VideoSeekDirection.NONE -> {

      }
      VideoSeekDirection.Forward -> {
        ShadowedIcon(
          Icons.Filled.FastForward,
          modifier = Modifier
            .align(Alignment.CenterEnd)
        )
      }
      VideoSeekDirection.Rewind -> {
        ShadowedIcon(
          Icons.Filled.FastRewind,
          modifier = Modifier
            .align(Alignment.CenterStart)
        )
      }
    }
  }
}