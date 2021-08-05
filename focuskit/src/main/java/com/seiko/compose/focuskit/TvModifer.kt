package com.seiko.compose.focuskit

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
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
  onFocusChanged: (TvFocusState) -> Unit
) = composed {
  val focusState: MutableState<TvFocusState?> = remember { mutableStateOf(null) }
  Modifier.onTvFocusEvent(focusItem) {
    if (focusState.value != it) {
      focusState.value = it
      onFocusChanged(it)
    }
  }
}

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.onTvFocusEvent(
  focusItem: TvFocusItem,
  onFocusEvent: FocusEventCallback
) = composed {
  DisposableEffect(focusItem.id) {
    focusItem.focusEventDispatcher.addCallback(onFocusEvent)
    Logger.log(Log.DEBUG) { "$focusItem add focus callback:${onFocusEvent.hashCode()}" }
    onDispose {
      focusItem.focusEventDispatcher.removeCallback(onFocusEvent)
      Logger.log(Log.DEBUG) { "$focusItem remove focus callback:${onFocusEvent.hashCode()}" }
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
    Logger.log(Log.DEBUG) { "$focusItem add key callback:${onKeyHandlerCallback.hashCode()}" }
    onDispose {
      focusItem.focusKeyHandlerDispatcher.removeCallback(onKeyHandlerCallback)
      Logger.log(Log.DEBUG) { "$focusItem remove key callback:${onKeyHandlerCallback.hashCode()}" }
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
      focusItem.focusHandler = focusHandler()
      Logger.log(Log.INFO) { "$focusItem set TvFocusHandler(${focusItem.focusHandler})" }
      rootTvViewItem.refocus()
    }
  }
  this
}

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.tvFocusable(
  requester: FocusRequester? = null,
) = composed {
  val focusRequester = requester ?: remember { FocusRequester() }
  val rootItem = LocalRootTvFocusItem.current

  SideEffect {
    rootItem.isFocusable = true
    focusRequester.requestFocus()
  }

  this
    .focusTarget()
    .focusRequester(focusRequester)
    .onFocusChanged {
      if (it.isFocused) {
        rootItem.refocus()
      }
    }
    .onKeyEvent {
      val key = controllerKey(it) ?: return@onKeyEvent false
      Logger.log(Log.DEBUG) { "action($key)" }
      rootItem.handleKey(key)
    }
}
