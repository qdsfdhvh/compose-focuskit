package com.seiko.compose.focuskit

import android.annotation.SuppressLint
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type

val LocalRootTvFocusItem = compositionLocalOf { RootTvFocusItem() }

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.onTvFocusChanged(
  focusItem: TvFocusItem,
  onFocusChanged: FocusChangedCallback
) = composed {
  DisposableEffect(focusItem.id) {
    focusItem.focusChangedDispatcher.addCallback(onFocusChanged)
    onFocusChanged(focusItem.focusState)
    onDispose {
      focusItem.focusChangedDispatcher.removeCallback(onFocusChanged)
    }
  }
  this
}

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.onTvKeyHandler(
  focusItem: TvFocusItem,
  onKeyHandlerCallback: FocusKeyHandlerCallback
) = composed {
  DisposableEffect(focusItem.id) {
    focusItem.focusKeyHandlerDispatcher.addCallback(onKeyHandlerCallback)
    onDispose {
      focusItem.focusKeyHandlerDispatcher.removeCallback(onKeyHandlerCallback)
    }
  }
  this
}

inline fun Modifier.handleTvKey(
  focusItem: TvFocusItem,
  key: TvControllerKey,
  crossinline onAction: () -> Boolean
) = onTvKeyHandler(focusItem) { eventKey, _ ->
  if (eventKey == key) {
    onAction()
  } else false
}

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.tvFocusable(
  focusItem: TvFocusItem,
  focusHandler: (() -> TvFocusHandler)? = null
) = composed {
  val rootTvViewItem = LocalRootTvFocusItem.current
  SideEffect {
    Logger.d("$focusItem set TvFocusHandler...")
    focusHandler?.invoke()?.let {
      Logger.d("$focusItem set TvFocusHandler")
      focusItem.focusHandler = it
    }
    rootTvViewItem.refocus()
  }
  this
}

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.tvFocusable(
  requester: FocusRequester?,
  rootItem: RootTvFocusItem? = null,
) = composed {
  val focusRequester = requester ?: remember { FocusRequester() }

  val rootFocusItem = rootItem ?: LocalRootTvFocusItem.current

  SideEffect {
    rootFocusItem.isFocusable = true
    focusRequester.requestFocus()
  }
  this
    .focusTarget()
    .focusRequester(focusRequester)
    .onFocusChanged {
      if (it.isFocused) {
        rootFocusItem.refocus()
      }
    }
    .onKeyEvent {
      if (KeyEventType.KeyUp == it.type) return@onKeyEvent false
      val key = it.nativeKeyEvent.keyCode.toControllerKey()
        ?: return@onKeyEvent false
      Logger.d("controllerKey=$key")
      rootFocusItem.handleKey(key)
    }
}

private fun RootTvFocusItem.handleKey(key: TvControllerKey): Boolean {
  if (!isFocusable) return false

  getFocusPath().asReversed().forEach { item ->
    Logger.d("$item handleKey($key, $this)")
    if (item.focusHandler.handleKey(key, this)) {
      return true
    }
  }
  return false
}