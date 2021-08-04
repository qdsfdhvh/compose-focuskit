package com.seiko.compose.focuskit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberContainerTvFocusItem(
  key: Any? = null,
  container: ContainerTvFocusItem? = null,
): ContainerTvFocusItem {
  val containerItem = container ?: LocalRootTvFocusItem.current
  return remember(key) {
    ContainerTvFocusItem().apply {
      containerItem.addChild(this)
    }
  }
}

@Composable
fun rememberTvFocusItem(
  key: Any? = null,
  container: ContainerTvFocusItem? = null,
): TvFocusItem {
  val containerItem = container ?: LocalRootTvFocusItem.current
  return remember(key) {
    TvFocusItem().apply {
      containerItem.addChild(this)
    }
  }
}
