package com.seiko.compose.player.internal

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.video.VideoSize
import com.seiko.compose.player.VideoPlayerController
import com.seiko.compose.player.VideoPlayerState
import com.seiko.compose.player.VideoSeekDirection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

internal class DefaultVideoPlayerController(
  private val exoPlayer: ExoPlayer,
  coroutineScope: CoroutineScope,
  initialState: VideoPlayerState,
) : VideoPlayerController {

  private val _state = MutableStateFlow(initialState)

  override val state: StateFlow<VideoPlayerState>
    get() = _state.asStateFlow()

  val currentState: VideoPlayerState
    get() = _state.value

  private val playerListener = object : Player.Listener {
    override fun onEvents(player: Player, events: Player.Events) {
      if (
        events.containsAny(
          Player.EVENT_IS_LOADING_CHANGED,
          Player.EVENT_PLAYBACK_STATE_CHANGED,
          Player.EVENT_PLAY_WHEN_READY_CHANGED,
          Player.EVENT_IS_PLAYING_CHANGED
        )
      ) {
        seekFlow.send(Unit)
      }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
      updatePlaybackState(playbackState)
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
      updatePlayState(playWhenReady)
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
      updateVideoSize(videoSize.width, videoSize.height)
    }
  }

  private val seekFlow = DebounceFlow<Unit>(250)

  init {
    with(exoPlayer) {
      playWhenReady = initialState.isPlaying
      addListener(playerListener)
    }
    seekFlow
      .onEach { updateDurationAndPosition() }
      .launchIn(coroutineScope)
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

  override fun quickSeekRewind() {
    if (currentState.seekDirection !== VideoSeekDirection.NONE) {
      return
    }
    val target = (exoPlayer.currentPosition - 10_000).coerceAtLeast(0)
    exoPlayer.seekTo(target)
    updateDurationAndPosition()
    updateSeekAction(VideoSeekDirection.Rewind)
  }

  override fun quickSeekForward() {
    if (currentState.seekDirection !== VideoSeekDirection.NONE) {
      return
    }
    val target = (exoPlayer.currentPosition + 10_000).coerceAtMost(exoPlayer.duration)
    exoPlayer.seekTo(target)
    updateDurationAndPosition()
    updateSeekAction(VideoSeekDirection.Forward)
  }

  override fun seekTo(positionMs: Long) {
    exoPlayer.seekTo(positionMs)
  }

  override fun reset() {
    exoPlayer.stop()
  }

  override fun showControl() {
    _state.set { copy(controlsVisible = true) }
  }

  override fun hideControl() {
    _state.set { copy(controlsVisible = false) }
  }

  private fun updateDurationAndPosition() {
    _state.set {
      copy(
        duration = exoPlayer.duration.coerceAtLeast(0L),
        currentPosition = exoPlayer.currentPosition.coerceAtLeast(0L),
        bufferedPosition = exoPlayer.bufferedPosition.coerceAtLeast(0L),
      )
    }
  }

  private fun updatePlaybackState(playbackState: Int) {
    _state.set { copy(playbackState = playbackState) }
  }

  private fun updatePlayState(playWhenReady: Boolean) {
    _state.set { copy(isPlaying = playWhenReady) }
  }

  private fun updateVideoSize(width: Int, height: Int) {
    _state.set { copy(videoSize = width to height) }
  }

  private fun updateSeekAction(seekAction: VideoSeekDirection) {
    _state.set { copy(seekDirection = seekAction) }
  }
}