package com.seiko.compose.focuskit.internal

import com.seiko.compose.focuskit.FocusEventCallback
import com.seiko.compose.focuskit.FocusEventDispatcher
import com.seiko.compose.focuskit.TvFocusItem
import com.seiko.compose.focuskit.TvFocusState

internal class FocusEventDispatcherImpl : FocusEventDispatcher {
  private val callbacks = mutableListOf<FocusEventCallback>()

  override fun addCallback(callback: FocusEventCallback) {
    callbacks.add(callback)
  }

  override fun removeCallback(callback: FocusEventCallback) {
    callbacks.remove(callback)
  }

  override fun onFocusEvent(state: TvFocusState) {
    callbacks.forEach { it(state) }
  }
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun TvFocusItem.onFocusEvent(state: TvFocusState) {
  focusEventDispatcher.onFocusEvent(state)
}