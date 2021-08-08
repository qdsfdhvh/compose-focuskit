package com.seiko.compose.focuskit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun rememberRootTvFocusItem(): RootTvFocusItem {
  return rememberSaveable(saver = rootTvFocusItemSaver()) {
    createRootTvFocusItem()
  }
}

private fun createRootTvFocusItem() = RootTvFocusItem()

private fun rootTvFocusItemSaver(): Saver<RootTvFocusItem, *> = Saver(
  save = { it.saveState() },
  restore = { createRootTvFocusItem().apply { restoreState(it) } }
)

@Composable
fun rememberContainerTvFocusItem(
  key: Any? = null,
  container: ContainerTvFocusItem? = null,
  index: Int? = null,
): ContainerTvFocusItem {
  val containerItem = container ?: LocalRootTvFocusItem.current
  return remember(key) {
    containerItem.getOrCreateChild(index) {
      ContainerTvFocusItem()
    }
  }
}

@Composable
fun rememberTvFocusItem(
  key: Any? = null,
  container: ContainerTvFocusItem,
  index: Int?,
): TvFocusItem {
  return remember(key) {
    container.getOrCreateChild(index) {
      TvFocusItem()
    }
  }
}