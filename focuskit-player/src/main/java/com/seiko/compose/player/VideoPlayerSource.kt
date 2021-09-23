package com.seiko.compose.player

import androidx.annotation.RawRes

sealed class VideoPlayerSource {

  data class Network(
    val url: String,
    val headers: Map<String, String> = emptyMap()
  ) : VideoPlayerSource()

  data class Raw(
    @RawRes val resId: Int
  ) : VideoPlayerSource()
}