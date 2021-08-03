package com.seiko.compose.focuskit

typealias FocusKeyHandlerCallback = (key: TvControllerKey, rootViewItem: RootTvFocusItem) -> Boolean

class FocusKeyHandlerDispatcher {
  private val callbacks = mutableListOf<FocusKeyHandlerCallback>()

  fun addCallback(callback: FocusKeyHandlerCallback) {
    callbacks.add(callback)
  }

  fun removeCallback(callback: FocusKeyHandlerCallback) {
    callbacks.remove(callback)
  }

  fun handleKey(key: TvControllerKey, rootViewItem: RootTvFocusItem): Boolean {
    callbacks.forEach { callback ->
      if (callback(key, rootViewItem)) {
        return true
      }
    }
    return false
  }
}

interface FocusKeyHandlerDispatcherOwner {
  val focusKeyHandlerDispatcher: FocusKeyHandlerDispatcher
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun TvFocusItem.handleKey(
  key: TvControllerKey,
  rootViewItem: RootTvFocusItem
): Boolean {
  return focusKeyHandlerDispatcher.handleKey(key, rootViewItem)
}