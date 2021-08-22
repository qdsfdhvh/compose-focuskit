package com.seiko.compose.player.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seiko.compose.player.LocalVideoPlayerController
import com.seiko.compose.player.internal.getDurationString

@Composable
fun MediaControlLayout(modifier: Modifier = Modifier) {
  val controller = LocalVideoPlayerController.current
  val state by controller.state.collectAsState()

  if (!state.controlsVisible) {
    return
  }

  val position = remember(state.currentPosition) { getDurationString(state.currentPosition) }
  val duration = remember(state.duration) { getDurationString(state.duration) }

  Box(modifier = modifier) {
    Column(modifier = Modifier.align(Alignment.BottomCenter).padding(4.dp)) {
      TimeTextBar(
        position = position,
        duration = duration
      )
      AppSeekBar(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        secondaryProgress = state.bufferedPosition,
        progress = state.currentPosition,
        max = state.duration,
      )
    }
    PlayToggleButton(
      modifier = Modifier.align(Alignment.Center),
      isPlaying = state.isPlaying,
      playbackState = state.playbackState
    )
  }
}
