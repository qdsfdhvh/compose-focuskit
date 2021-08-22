package com.seiko.compose.player.ui

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.seiko.compose.focuskit.TvControllerKey
import com.seiko.compose.focuskit.onTvKeyEvent
import com.seiko.compose.player.LocalVideoPlayerController

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
            controller.quickSeekRewind()
            true
          }
          TvControllerKey.Right -> {
            controller.quickSeekForward()
            true
          }
          else -> false
        }
      }
      .focusable(),
  ) {

  }
}