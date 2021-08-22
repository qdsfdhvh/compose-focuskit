package com.seiko.compose.focuskit.demo.ui.foundation

// import android.content.Context
// import android.util.Log
// import androidx.compose.foundation.layout.*
// import androidx.compose.material.LinearProgressIndicator
// import androidx.compose.material.MaterialTheme
// import androidx.compose.material.Text
// import androidx.compose.runtime.*
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.focus.FocusRequester
// import androidx.compose.ui.focus.focusRequester
// import androidx.compose.ui.focus.focusTarget
// import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.platform.LocalContext
// import androidx.compose.ui.platform.LocalLifecycleOwner
// import androidx.compose.ui.tooling.preview.Preview
// import androidx.compose.ui.unit.dp
// import androidx.compose.ui.viewinterop.AndroidView
// import androidx.lifecycle.Lifecycle
// import androidx.lifecycle.LifecycleObserver
// import androidx.lifecycle.OnLifecycleEvent
// import com.google.android.exoplayer2.*
// import com.google.android.exoplayer2.Player.*
// import com.google.android.exoplayer2.source.ProgressiveMediaSource
// import com.google.android.exoplayer2.ui.StyledPlayerView
// import com.google.android.exoplayer2.ui.TimeBar
// import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
// import com.seiko.compose.focuskit.TvControllerKey
// import com.seiko.compose.focuskit.demo.LocalAppNavigator
// import com.seiko.compose.focuskit.onTvKeyEvent
//
// private const val TAG = "TvPlayer"
//
// @Composable
// private fun rememberExoplayer(context: Context, url: String): ExoPlayer {
//   return remember(url) {
//     Log.d(TAG, "创建exoplayer")
//
//     val media = ProgressiveMediaSource.Factory(DefaultDataSourceFactory(context))
//       .createMediaSource(MediaItem.fromUri(url))
//
//     SimpleExoPlayer.Builder(context)
//       .build()
//       .apply {
//         setMediaSource(media)
//         prepare()
//       }
//   }
// }
//
// @Composable
// fun TvVideoPlayer(url: String) {
//   val context = LocalContext.current
//   val lifecycle = LocalLifecycleOwner.current.lifecycle
//
//   var autoPlay = remember(url) { true }
//   var window = remember(url) { 0 }
//   var position = remember(url) { 0L }
//
//   val player = rememberExoplayer(context, url)
//   // SideEffect {
//   //   player.playWhenReady = autoPlay
//   //   player.seekTo(window, position)
//   // }
//
//   // val controller = remember(player) {
//   //   ForwardingPlayer(player)
//   // }
//
//   fun updateState() {
//     autoPlay = player.playWhenReady
//     window = player.currentWindowIndex
//     position = 0L.coerceAtLeast(player.contentPosition)
//   }
//
//   fun updateProgress() {
//
//   }
//
//   val listener = remember(url) {
//     object : ComponentListener {
//       override fun onEvents(player: Player, events: Events) {
//         if (events.containsAny(
//             EVENT_PLAYBACK_STATE_CHANGED,
//             EVENT_PLAY_WHEN_READY_CHANGED,
//             EVENT_IS_PLAYING_CHANGED
//           )
//         ) {
//           updateProgress()
//         }
//       }
//
//       override fun onTimelineChanged(timeline: Timeline, reason: Int) {
//         super.onTimelineChanged(timeline, reason)
//       }
//     }
//   }
//
//   var openDialog by remember { mutableStateOf(false) }
//   var openControl by remember { mutableStateOf(false) }
//
//   val focusRequester = remember { FocusRequester() }
//
//   Box(
//     modifier = Modifier
//       .onTvKeyEvent {
//         when (it) {
//           TvControllerKey.Back -> {
//             if (openControl) {
//               openControl = false
//               focusRequester.requestFocus()
//               return@onTvKeyEvent true
//             }
//             if (!openDialog) {
//               openDialog = true
//               return@onTvKeyEvent true
//             }
//           }
//           TvControllerKey.Enter -> {
//             if (!openControl) {
//               openControl = true
//               return@onTvKeyEvent true
//             }
//           }
//           else -> {
//           }
//         }
//         false
//       }
//       .focusRequester(focusRequester)
//       .focusTarget(),
//   ) {
//     AndroidView(
//       modifier = Modifier.fillMaxSize(),
//       factory = {
//         StyledPlayerView(it).also { playerView ->
//           playerView.useController = false
//           lifecycle.addObserver(object : LifecycleObserver {
//             @OnLifecycleEvent(Lifecycle.Event.ON_START)
//             fun onStart() {
//               playerView.keepScreenOn = true
//               playerView.onResume()
//               player.playWhenReady = autoPlay
//             }
//
//             @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//             fun onStop() {
//               updateState()
//               playerView.keepScreenOn = false
//               playerView.onPause()
//               player.playWhenReady = false
//             }
//           })
//         }
//       },
//     ) { playerView ->
//       playerView.player = player
//     }
//
//     if (openControl) {
//       val controllerFocusRequester = remember { FocusRequester() }
//
//       TvVideoControlPlayer(
//         modifier = Modifier
//           .focusRequester(controllerFocusRequester)
//           .align(Alignment.BottomCenter)
//           .onTvKeyEvent {
//             when (it) {
//               TvControllerKey.Enter -> {
//                 openControl = false
//                 focusRequester.requestFocus()
//                 return@onTvKeyEvent true
//               }
//               TvControllerKey.Left -> {
//                 // controller.seekTo(player.contentPosition + 2000)
//               }
//               TvControllerKey.Right -> {
//                 // controller.seekTo(player.contentPosition - 2000)
//               }
//               else -> {
//               }
//             }
//             false
//           },
//         progress = 0.2f,
//       )
//
//       SideEffect {
//         controllerFocusRequester.requestFocus()
//         Log.d(TAG, "打开控制界面")
//       }
//     }
//
//     // 关闭选择框
//     if (openDialog) {
//       val navController = LocalAppNavigator.current
//       TvSelectDialog(
//         text = "是否退出播放？",
//         onCenterClick = {
//           openDialog = false
//           navController.popBackStack()
//         },
//         onCancelClick = {
//           openDialog = false
//         },
//       )
//     }
//   }
//
//   LaunchedEffect(url) {
//     player.addListener(listener)
//   }
//
//   DisposableEffect(url) {
//     onDispose {
//       updateState()
//       player.release()
//     }
//   }
//
//   LaunchedEffect(Unit) {
//     focusRequester.requestFocus()
//   }
// }
//
// @Composable
// fun TvVideoControlPlayer(
//   modifier: Modifier = Modifier,
//   progress: Float,
// ) {
//   Column(
//     modifier = modifier
//       .onTvKeyEvent {
//         false
//       }
//       .focusTarget()
//   ) {
//     Row(
//       modifier = Modifier
//         .fillMaxWidth()
//         .padding(horizontal = 5.dp),
//       horizontalArrangement = Arrangement.SpaceBetween
//     ) {
//       Text(
//         text = "00:00",
//         color = Color.White,
//         style = MaterialTheme.typography.subtitle1,
//       )
//       Text(
//         text = "12:00",
//         color = Color.White,
//         style = MaterialTheme.typography.subtitle1,
//       )
//     }
//     LinearProgressIndicator(
//       modifier = Modifier
//         .fillMaxWidth()
//         .padding(5.dp),
//       progress = progress,
//       backgroundColor = Color.Gray.copy(alpha = 0.4f)
//     )
//   }
// }
//
// private interface ComponentListener : Listener, TimeBar.OnScrubListener {
//   override fun onScrubStart(timeBar: TimeBar, position: Long) {}
//   override fun onScrubMove(timeBar: TimeBar, position: Long) {}
//   override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {}
// }
//
// @Preview
// @Composable
// fun TvVideoControlPlayerPreview() {
//   TvVideoControlPlayer(progress = 0.2f)
// }