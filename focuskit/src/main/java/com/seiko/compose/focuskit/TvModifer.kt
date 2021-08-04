package com.seiko.compose.focuskit

import android.annotation.SuppressLint
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.onKeyEvent

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

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.tvFocusable(
  focusItem: TvFocusItem,
  focusHandler: (() -> TvFocusHandler)? = null
) = composed {
  if (focusHandler != null) {
    val rootTvViewItem = LocalRootTvFocusItem.current
    LaunchedEffect(focusItem) {
      Logger.d("$focusItem set TvFocusHandler")
      focusItem.focusHandler = focusHandler()
      rootTvViewItem.refocus()
    }
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
  // TODO provide root

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
      val key = controllerKey(it) ?: return@onKeyEvent false
      Logger.d("action($key)")
      rootFocusItem.handleKey(key)
    }
}
