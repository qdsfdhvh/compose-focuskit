package com.seiko.compose.player.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.seiko.compose.player.LocalVideoPlayerController
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

@Composable
fun VlcMediaPlayerLayout(mediaPlayer: MediaPlayer, modifier: Modifier = Modifier) {
  val controller = LocalVideoPlayerController.current
  val state by controller.state.collectAsState()

  val lifecycle = LocalLifecycleOwner.current.lifecycle

  VlcPlayerSurface(modifier) { videoLayout ->
    lifecycle.addObserver(object : LifecycleObserver {
      @OnLifecycleEvent(Lifecycle.Event.ON_START)
      fun onStart() {
        mediaPlayer.attachViews(videoLayout, null, true, true)
        videoLayout.keepScreenOn = true
        if (state.isPlaying) {
          controller.play()
        }
      }

      @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
      fun onStop() {
        mediaPlayer.detachViews()
        videoLayout.keepScreenOn = false
        controller.pause()
      }
    })
  }

  DisposableEffect(Unit) {
    onDispose {
      if (mediaPlayer.hasMedia()) {
        mediaPlayer.media?.release()
      }
      mediaPlayer.release()
    }
  }
}

@Composable
fun VlcPlayerSurface(
  modifier: Modifier = Modifier,
  onPlayerViewAvailable: (VLCVideoLayout) -> Unit = {}
) {
  AndroidView(
    modifier = modifier,
    factory = { context ->
      VLCVideoLayout(context).apply {
        onPlayerViewAvailable(this)
      }
    }
  )
}
