package com.seiko.compose.player.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppSeekBar(
  progress: Long,
  max: Long,
  modifier: Modifier = Modifier,
  secondaryProgress: Long? = null,
  color: Color = MaterialTheme.colors.primary,
  secondaryColor: Color = Color.White.copy(alpha = 0.6f)
) {
  Canvas(modifier = modifier.height(4.dp)) {
    val radius = size.height / 2
    drawRoundRect(secondaryColor, cornerRadius = CornerRadius(radius))
    if (secondaryProgress != null) {
      drawRoundRect(
        color = color.copy(alpha = 0.6f),
        size = Size(secondaryProgress * size.width / max, size.height),
        cornerRadius = CornerRadius(radius)
      )
    }
    drawRoundRect(
      color = color,
      size = Size(progress * size.width / max, size.height),
      cornerRadius = CornerRadius(radius)
    )
  }
}