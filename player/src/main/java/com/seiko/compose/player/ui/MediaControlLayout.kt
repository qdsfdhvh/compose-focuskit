package com.seiko.compose.player.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seiko.compose.player.LocalVideoPlayerController
import com.seiko.compose.player.internal.getDurationString

@Composable
fun MediaControlLayout(modifier: Modifier = Modifier) {
  val controller = LocalVideoPlayerController.current
  val state by controller.state.collectAsState()

  val isSeeking by remember(state.seekDirection) {
    mutableStateOf(state.seekDirection.isSeeking)
  }

  if (!state.controlsVisible && !isSeeking) {
    return
  }

  val position = remember(state.currentPosition) { getDurationString(state.currentPosition) }
  val duration = remember(state.duration) { getDurationString(state.duration) }

  Box(modifier = modifier) {

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .padding(4.dp)
    ) {
      TimeTextBar(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 4.dp),
        position = position,
        duration = duration
      )
      SmallSeekBar(
        modifier = Modifier
          .fillMaxWidth(),
        secondaryProgress = state.bufferedPosition,
        progress = state.currentPosition,
        max = state.duration,
      )
    }

    if (!isSeeking) {
      PlayToggleButton(
        modifier = Modifier.align(Alignment.Center),
        isPlaying = state.isPlaying,
        playbackState = state.playbackState
      )
    }
  }
}
