package com.seiko.compose.focuskit.internal

import com.seiko.compose.focuskit.FocusKeyHandlerCallback
import com.seiko.compose.focuskit.FocusKeyHandlerDispatcher
import com.seiko.compose.focuskit.RootTvFocusItem
import com.seiko.compose.focuskit.TvControllerKey
import com.seiko.compose.focuskit.TvFocusItem

internal class FocusKeyHandlerDispatcherImpl : FocusKeyHandlerDispatcher {
  private val callbacks = mutableListOf<FocusKeyHandlerCallback>()

  override fun addCallback(callback: FocusKeyHandlerCallback) {
    callbacks.add(callback)
  }

  override fun removeCallback(callback: FocusKeyHandlerCallback) {
    callbacks.remove(callback)
  }

  override fun handleKey(key: TvControllerKey, rootViewItem: RootTvFocusItem): Boolean {
    callbacks.forEach { callback ->
      if (callback(key, rootViewItem)) return true
    }
    return false
  }
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun TvFocusItem.handleKey(
  key: TvControllerKey,
  rootViewItem: RootTvFocusItem
): Boolean {
  return focusKeyHandlerDispatcher.handleKey(key, rootViewItem)
}