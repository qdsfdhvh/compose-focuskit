package com.seiko.compose.focuskit

import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type
import android.view.KeyEvent as NativeKeyEvent

enum class TvControllerKey {
  Up, Down, Left, Right, Enter, Back, Menu, Play
}

fun controllerKey(keyEvent: NativeKeyEvent): TvControllerKey? = when (keyEvent.keyCode) {
  NativeKeyEvent.KEYCODE_DPAD_UP -> TvControllerKey.Up
  NativeKeyEvent.KEYCODE_DPAD_DOWN -> TvControllerKey.Down
  NativeKeyEvent.KEYCODE_DPAD_LEFT -> TvControllerKey.Left
  NativeKeyEvent.KEYCODE_DPAD_RIGHT -> TvControllerKey.Right
  NativeKeyEvent.KEYCODE_DPAD_CENTER,
  NativeKeyEvent.KEYCODE_BUTTON_SELECT,
  NativeKeyEvent.KEYCODE_ENTER,
  NativeKeyEvent.KEYCODE_BUTTON_A -> TvControllerKey.Enter
  NativeKeyEvent.KEYCODE_BACK -> TvControllerKey.Back
  NativeKeyEvent.KEYCODE_MEDIA_PLAY -> TvControllerKey.Play
  else -> null
}

fun controllerKey(event: KeyEvent): TvControllerKey? {
  if (event.type == KeyEventType.KeyUp) {
    return null
  }
  return controllerKey(event.nativeKeyEvent)
}