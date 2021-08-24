package com.seiko.compose.player.ui

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.seiko.compose.focuskit.TvControllerKey
import com.seiko.compose.focuskit.onTvKeyEvent
import com.seiko.compose.player.LocalVideoPlayerController
import com.seiko.compose.player.VideoSeekDirection

@Composable
fun MediaControlKeyEvent(modifier: Modifier = Modifier) {
  val controller = LocalVideoPlayerController.current
  val state by controller.state.collectAsState()

  val focusRequester = remember { FocusRequester() }

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
          TvControllerKey.Back -> {
            if (state.controlsVisible) {
              controller.hideControl()
              true
            } else false
          }
          else -> false
        }
      }
      .focusRequester(focusRequester)
      .focusable(),
  ) {
    VideoSeekAnimation(
      modifier = Modifier.matchParentSize(),
      seekDirection = state.seekDirection,
    )
  }

  SideEffect {
    focusRequester.requestFocus()
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