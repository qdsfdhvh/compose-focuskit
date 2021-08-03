package com.seiko.compose.focuskit.demo.ui.foundation

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.seiko.compose.focuskit.demo.R

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalCoilApi::class)
@Composable
fun NetworkImage(
  data: Any,
  modifier: Modifier = Modifier,
  contentScale: ContentScale = ContentScale.Crop,
  placeholder: @Composable (() -> Unit)? = null,
) {
  val painter = if (data is Painter) {
    data
  } else {
    val imageLoader = coil.compose.LocalImageLoader.current
    rememberImagePainter(
      data = data,
      imageLoader = imageLoader,
      builder = {
        crossfade(true)
      }
    )
  }
  if (painter is ImagePainter && painter.state is ImagePainter.State.Loading) {
    placeholder?.invoke()
  }
  Image(
    painter = painter,
    modifier = modifier,
    contentScale = contentScale,
    contentDescription = stringResource(id = R.string.common_network_image)
  )
}