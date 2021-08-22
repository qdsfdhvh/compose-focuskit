package com.seiko.compose.player.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.exoplayer2.Player
import com.seiko.compose.player.LocalVideoPlayerController

@Composable
fun PlayToggleButton(
  modifier: Modifier = Modifier,
  isPlaying: Boolean,
  playbackState: Int,
) {
  IconButton(
    modifier = modifier,
    onClick = { /* nothing to do */ },
  ) {

    when (playbackState) {
      Player.STATE_READY -> {
        if (!isPlaying) {
          ShadowedIcon(icon = Icons.Filled.Pause)
        }
      }
      Player.STATE_ENDED -> {
        ShadowedIcon(icon = Icons.Filled.Restore)
      }
      Player.STATE_BUFFERING -> {
        CircularProgressIndicator()
      }
      else -> {
        ShadowedIcon(icon = Icons.Filled.PlayArrow)
      }
    }
  }
}


@Composable
fun ShadowedIcon(
  icon: ImageVector,
  modifier: Modifier = Modifier,
  iconSize: Dp = 48.dp,
) {
  Box(modifier = modifier) {
    Icon(
      imageVector = icon,
      tint = Color.White.copy(alpha = 0.3f),
      modifier = Modifier
        .size(iconSize)
        .offset(2.dp, 2.dp)
        .then(modifier),
      contentDescription = null
    )
    Icon(
      imageVector = icon,
      tint = Color.White,
      modifier = Modifier.size(iconSize),
      contentDescription = null
    )
  }
}