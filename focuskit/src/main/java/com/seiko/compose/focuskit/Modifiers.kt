package com.seiko.compose.focuskit

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent

inline fun Modifier.onPreviewTvKeyEvent(
  crossinline onPreviewKeyEvent: (TvKeyEvent) -> Boolean
) = this.onPreviewKeyEvent {
  controllerKey(it)?.run(onPreviewKeyEvent) ?: false
}

inline fun Modifier.onTvKeyEvent(
  crossinline onKeyEvent: (TvKeyEvent) -> Boolean
) = this.onKeyEvent {
  controllerKey(it)?.run(onKeyEvent) ?: false
}

inline fun Modifier.handleTvKey(
  key: TvKeyEvent,
  crossinline onAction: () -> Boolean
) = this.onTvKeyEvent {
  if (it == key) onAction()
  else false
}

fun Modifier.focusClick(onClick: () -> Unit) =
  this
    .clickable(onClick = onClick)
    .handleTvKey(TvKeyEvent.Enter) {
      onClick()
      true
    }

@Composable
fun <T : Any> rememberFocusRequesters(array: Array<T>) = remember(array) {
  List(array.size) { FocusRequester() }
}

@Composable
fun <T : Any> rememberFocusRequesters(list: Collection<T>) = remember(list) {
  List(list.size) { FocusRequester() }
}

@Composable
fun rememberFocusRequesters(size: Int) = remember(size) {
  List(size) { FocusRequester() }
}
