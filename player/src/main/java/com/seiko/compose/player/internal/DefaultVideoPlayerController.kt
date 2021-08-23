package com.seiko.compose.player.internal

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoSize
import com.seiko.compose.player.VideoPlayerController
import com.seiko.compose.player.VideoPlayerState
import com.seiko.compose.player.VideoSeekDirection
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultVideoPlayerController(
  private val exoPlayer: ExoPlayer,
  private val coroutineScope: CoroutineScope,
  initialState: VideoPlayerState,
) : VideoPlayerController {

  private val intents = Channel<Action>(
    onBufferOverflow = BufferOverflow.DROP_LATEST
  )

  @OptIn(ExperimentalCoroutinesApi::class)
  private val _state = intents.receiveAsFlow()
    .scan(initialState) { state, action ->
      when (action) {
        is Action.ControlsVisible -> state.copy(controlsVisible = action.isVisible)
        is Action.Progress -> state.copy(
          duration = action.duration,
          currentPosition = action.currentPosition,
          bufferedPosition = action.bufferedPosition
        )
        is Action.PlaybackState -> state.copy(playbackState = action.playbackState)
        is Action.PlayState -> state.copy(isPlaying = action.isPlaying)
        is Action.VideoSize -> state.copy(videoSize = action.videoSize)
        is Action.SeekDirection -> state.copy(seekDirection = action.seekDirection)
      }
    }
    .stateIn(coroutineScope, SharingStarted.Lazily, initialState)

  override val state: StateFlow<VideoPlayerState>
    get() = _state

  val currentState: VideoPlayerState
    get() = _state.value

  private val playerListener = object : Player.Listener {

    override fun onPlaybackStateChanged(playbackState: Int) {
      if (playbackState == Player.STATE_READY) {
        seekFinish()
        updateProgress()
      }
      updatePlaybackState(playbackState)
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
      updatePlayState(playWhenReady)
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
      updateVideoSize(videoSize.width, videoSize.height)
    }
  }

  init {
    exoPlayer.playWhenReady = initialState.isPlaying
    exoPlayer.addListener(playerListener)
  }

  override fun play() {
    exoPlayer.playWhenReady = true
  }

  override fun pause() {
    exoPlayer.playWhenReady = false
  }

  override fun playToggle() {
    if (exoPlayer.isPlaying) pause()
    else play()
  }

  override fun seekRewind() {
    val target = (exoPlayer.currentPosition - 10_000).coerceAtLeast(0)
    exoPlayer.seekTo(target)
    updateDurationAndPosition()
    updateSeekAction(VideoSeekDirection.Rewind)
  }

  override fun seekForward() {
    val target = (exoPlayer.currentPosition + 10_000).coerceAtMost(exoPlayer.duration)
    exoPlayer.seekTo(target)
    updateDurationAndPosition()
    updateSeekAction(VideoSeekDirection.Forward)
  }

  override fun seekFinish() {
    updateSeekAction(VideoSeekDirection.NONE)
  }

  override fun seekTo(positionMs: Long) {
    exoPlayer.seekTo(positionMs)
  }

  override fun reset() {
    exoPlayer.stop()
  }

  override fun showControl() {
    intents.trySend(Action.ControlsVisible(true))
  }

  override fun hideControl() {
    intents.trySend(Action.ControlsVisible(false))
  }

  private var updateProgressJob: Job? = null
  private fun updateProgress() {
    updateProgressJob?.cancel()
    updateProgressJob = coroutineScope.launch {
      while (isActive) {
        updateDurationAndPosition()
        delay(250)
      }
    }
  }

  private fun updateDurationAndPosition() {
    intents.trySend(
      Action.Progress(
        duration = exoPlayer.duration.coerceAtLeast(0L),
        currentPosition = exoPlayer.currentPosition.coerceAtLeast(0L),
        bufferedPosition = exoPlayer.bufferedPosition.coerceAtLeast(0L),
      )
    )
  }

  private fun updatePlaybackState(playbackState: Int) {
    intents.trySend(Action.PlaybackState(playbackState))
  }

  private fun updatePlayState(playWhenReady: Boolean) {
    intents.trySend(Action.PlayState(playWhenReady))
  }

  private fun updateVideoSize(width: Int, height: Int) {
    intents.trySend(Action.VideoSize(width to height))
  }

  private fun updateSeekAction(seekAction: VideoSeekDirection) {
    intents.trySend(Action.SeekDirection(seekAction))
  }

  private sealed class Action {

    data class Progress(
      val duration: Long,
      val currentPosition: Long,
      val bufferedPosition: Long,
    ) : Action()

    data class ControlsVisible(val isVisible: Boolean) : Action()
    data class PlaybackState(val playbackState: Int) : Action()
    data class PlayState(val isPlaying: Boolean) : Action()
    data class VideoSize(val videoSize: Pair<Int, Int>) : Action()
    data class SeekDirection(val seekDirection: VideoSeekDirection) : Action()
  }
}