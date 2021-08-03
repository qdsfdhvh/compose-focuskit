package com.seiko.compose.focuskit

typealias FocusChangedCallback = (TvFocusState) -> Unit

class FocusChangedDispatcher {
  private val callbacks = mutableListOf<FocusChangedCallback>()

  fun addCallback(callback: FocusChangedCallback) {
    callbacks.add(callback)
  }

  fun removeCallback(callback: FocusChangedCallback) {
    callbacks.remove(callback)
  }

  fun onFocusChanged(state: TvFocusState) {
    callbacks.forEach { it(state) }
  }
}

interface FocusChangedDispatcherOwner {
  val focusChangedDispatcher: FocusChangedDispatcher
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun TvFocusItem.onFocusChanged(state: TvFocusState) {
  focusChangedDispatcher.onFocusChanged(state)
}