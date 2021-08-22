package com.seiko.compose.player

import kotlinx.coroutines.flow.StateFlow

interface VideoPlayerController {
  val state: StateFlow<VideoPlayerState>
  fun play()
  fun pause()
  fun playToggle()
  fun reset()
  fun seekTo(positionMs: Long)
  fun quickSeekForward()
  fun quickSeekRewind()
  fun showControl()
  fun hideControl()
}