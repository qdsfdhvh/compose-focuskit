package com.seiko.compose.focuskit

typealias FocusChangedCallback = (TvFocusState) -> Unit

interface FocusChangedDispatcher {
  fun addCallback(callback: FocusChangedCallback)
  fun removeCallback(callback: FocusChangedCallback)
  fun onFocusChanged(state: TvFocusState)
}

interface FocusChangedDispatcherOwner {
  val focusChangedDispatcher: FocusChangedDispatcher
}