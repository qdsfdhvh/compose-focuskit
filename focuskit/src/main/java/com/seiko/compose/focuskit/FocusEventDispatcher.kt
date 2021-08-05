package com.seiko.compose.focuskit

typealias FocusEventCallback = (TvFocusState) -> Unit

interface FocusEventDispatcher {
  fun addCallback(callback: FocusEventCallback)
  fun removeCallback(callback: FocusEventCallback)
  fun onFocusEvent(state: TvFocusState)
}

interface FocusChangedDispatcherOwner {
  val focusEventDispatcher: FocusEventDispatcher
}