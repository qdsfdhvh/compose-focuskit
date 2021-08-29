package com.seiko.compose.focuskit

import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type
import android.view.KeyEvent as NativeKeyEvent

enum class TvKeyEvent {
  Up, Down, Left, Right, Enter, Back, Menu, Play
}

fun controllerKey(event: NativeKeyEvent): TvKeyEvent? = when (event.keyCode) {
  NativeKeyEvent.KEYCODE_DPAD_UP -> TvKeyEvent.Up
  NativeKeyEvent.KEYCODE_DPAD_DOWN -> TvKeyEvent.Down
  NativeKeyEvent.KEYCODE_DPAD_LEFT -> TvKeyEvent.Left
  NativeKeyEvent.KEYCODE_DPAD_RIGHT -> TvKeyEvent.Right
  NativeKeyEvent.KEYCODE_DPAD_CENTER,
  NativeKeyEvent.KEYCODE_BUTTON_SELECT,
  NativeKeyEvent.KEYCODE_ENTER,
  NativeKeyEvent.KEYCODE_BUTTON_A -> TvKeyEvent.Enter
  NativeKeyEvent.KEYCODE_BACK -> TvKeyEvent.Back
  NativeKeyEvent.KEYCODE_MEDIA_PLAY -> TvKeyEvent.Play
  NativeKeyEvent.KEYCODE_MENU -> TvKeyEvent.Menu
  else -> null
}

fun controllerKey(event: KeyEvent): TvKeyEvent? {
  if (event.type == KeyEventType.KeyUp) {
    return null
  }
  return controllerKey(event.nativeKeyEvent)
}