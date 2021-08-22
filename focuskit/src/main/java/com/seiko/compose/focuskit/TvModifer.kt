package com.seiko.compose.focuskit

import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent

fun Modifier.tvFocusTarget(
  focusItem: TvFocusItem
) = this
  .focusRequester(focusItem.focusRequester)
  .focusTarget()

fun Modifier.onPreviewTvKeyEvent(
  onPreviewKeyEvent: (TvControllerKey) -> Boolean
) = this
  .onPreviewKeyEvent {
    val key = controllerKey(it) ?: return@onPreviewKeyEvent false
    onPreviewKeyEvent(key)
  }

fun Modifier.onTvKeyEvent(
  onKeyEvent: (TvControllerKey) -> Boolean
) = this
  .onKeyEvent {
    val key = controllerKey(it) ?: return@onKeyEvent false
    onKeyEvent(key)
  }

inline fun Modifier.handleTvKey(
  key: TvControllerKey,
  crossinline onAction: () -> Boolean
) = this.onTvKeyEvent {
  if (it == key) {
    onAction()
  } else {
    false
  }
}