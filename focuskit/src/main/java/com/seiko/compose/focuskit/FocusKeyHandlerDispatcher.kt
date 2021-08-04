package com.seiko.compose.focuskit

typealias FocusKeyHandlerCallback = (key: TvControllerKey, rootViewItem: RootTvFocusItem) -> Boolean

interface FocusKeyHandlerDispatcher {
  fun addCallback(callback: FocusKeyHandlerCallback)
  fun removeCallback(callback: FocusKeyHandlerCallback)
  fun handleKey(key: TvControllerKey, rootViewItem: RootTvFocusItem): Boolean
}

interface FocusKeyHandlerDispatcherOwner {
  val focusKeyHandlerDispatcher: FocusKeyHandlerDispatcher
}