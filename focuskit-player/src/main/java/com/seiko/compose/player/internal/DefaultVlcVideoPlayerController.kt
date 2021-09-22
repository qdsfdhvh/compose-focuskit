package com.seiko.compose.player.internal

import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoSize
import com.seiko.compose.player.VideoPlayerAction
import com.seiko.compose.player.VideoPlayerController
import com.seiko.compose.player.VideoPlayerState
import com.seiko.compose.player.VideoSeekDirection
import com.seiko.compose.player.stateReducer
import kotlin.math.abs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.videolan.libvlc.MediaPlayer

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultVlcVideoPlayerController(
  private val player: MediaPlayer,
  private val coroutineScope: CoroutineScope,
  initialState: VideoPlayerState,
) : VideoPlayerController {

  private val intents = Channel<VideoPlayerAction>(
    capacity = Channel.BUFFERED,
    onBufferOverflow = BufferOverflow.SUSPEND
  )

  @OptIn(ExperimentalCoroutinesApi::class)
  private val _state = intents.receiveAsFlow()
    .scan(initialState, ::stateReducer)
    .flowOn(Dispatchers.IO)
    .stateIn(coroutineScope, SharingStarted.Lazily, initialState)

  override val state: StateFlow<VideoPlayerState>
    get() = _state

  val currentState: VideoPlayerState
    get() = _state.value

  override val isPlaying: Boolean
    get() = player.isPlaying

  private var lastTime = 0L
  // private var lastPosition = 0F

  private val eventListener = MediaPlayer.EventListener { event ->
    if (event == null) return@EventListener
    when (event.type) {
      MediaPlayer.Event.TimeChanged -> {
        val time = event.timeChanged
        if (abs(time - lastTime) > 950L) {
          updateProgress(newTime = time)
          lastTime = time
        }
      }
      MediaPlayer.Event.LengthChanged -> {
        updateProgress(newLength = event.lengthChanged)
      }
      MediaPlayer.Event.PositionChanged -> {
        // lastPosition = event.positionChanged
      }
    }
  }

  //override fun onPlaybackStateChanged(playbackState: Int) {
  //       if (playbackState == Player.STATE_READY) {
  //         seekFinish()
  //         updateProgress()
  //       }
  //       updatePlaybackState(playbackState)
  //     }
  //
  //     override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
  //       updatePlayState(playWhenReady)
  //     }
  //
  //     override fun onVideoSizeChanged(videoSize: VideoSize) {
  //       updateVideoSize(videoSize.width, videoSize.height)
  //     }

  init {
    // player.playWhenReady = initialState.isPlaying
    player.setEventListener(eventListener)
  }

  override fun play() {
    player.play()
  }

  override fun pause() {
    player.pause()
  }

  override fun playToggle() {
    if (player.isPlaying) pause()
    else play()
  }

  override fun seekRewind() {
    val target = (player.currentPosition - 10_000).coerceAtLeast(0)
    // player.seekTo(target)
    player.position = target
    updateDurationAndPosition()
    updateSeekAction(VideoSeekDirection.Rewind)
  }

  override fun seekForward() {
    val target = (player.currentPosition + 10_000).coerceAtMost(player.duration)
    // player.seekTo(target)
    player.position = target
    updateDurationAndPosition()
    updateSeekAction(VideoSeekDirection.Forward)
  }

  override fun seekFinish() {
    updateSeekAction(VideoSeekDirection.NONE)
  }

  override fun seekTo(positionMs: Long) {
    // player.seekTo(positionMs)
    player.position = positionMs.toFloat()
  }

  override fun reset() {
    player.stop()
    lastTime = 0
  }

  override fun showControl() {
    intents.trySend(VideoPlayerAction.ControlsVisible(true))
  }

  override fun hideControl() {
    intents.trySend(VideoPlayerAction.ControlsVisible(false))
  }

  // private var updateProgressJob: Job? = null
  private fun updateProgress(newTime: Long = currentState.currentPosition, newLength: Long = currentState.duration) {
    // updateProgressJob?.cancel()
    // updateProgressJob = coroutineScope.launch {
    //   while (isActive) {
    //     updateDurationAndPosition()
    //     delay(250)
    //   }
    // }

  }

  private fun updateDurationAndPosition() {
    intents.trySend(
      VideoPlayerAction.Progress(
        duration = player.duration.coerceAtLeast(0L),
        currentPosition = player.currentPosition.coerceAtLeast(0L),
        bufferedPosition = player.bufferedPosition.coerceAtLeast(0L),
      )
    )
  }

  private fun updatePlaybackState(playbackState: Int) {
    intents.trySend(VideoPlayerAction.PlaybackState(playbackState))
  }

  private fun updatePlayState(playWhenReady: Boolean) {
    intents.trySend(VideoPlayerAction.PlayState(playWhenReady))
  }

  private fun updateVideoSize(width: Int, height: Int) {
    intents.trySend(VideoPlayerAction.VideoSize(width to height))
  }

  private fun updateSeekAction(seekAction: VideoSeekDirection) {
    intents.trySend(VideoPlayerAction.SeekDirection(seekAction))
  }
}
