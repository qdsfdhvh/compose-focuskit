package com.seiko.compose.focuskit.internal

import com.seiko.compose.focuskit.FocusChangedCallback
import com.seiko.compose.focuskit.FocusChangedDispatcher
import com.seiko.compose.focuskit.TvFocusItem
import com.seiko.compose.focuskit.TvFocusState

internal class FocusChangedDispatcherImpl : FocusChangedDispatcher {
  private val callbacks = mutableListOf<FocusChangedCallback>()

  override fun addCallback(callback: FocusChangedCallback) {
    callbacks.add(callback)
  }

  override fun removeCallback(callback: FocusChangedCallback) {
    callbacks.remove(callback)
  }

  override fun onFocusChanged(state: TvFocusState) {
    callbacks.forEach { it(state) }
  }
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun TvFocusItem.onFocusChanged(state: TvFocusState) {
  focusChangedDispatcher.onFocusChanged(state)
}