package com.seiko.compose.focuskit

import android.view.KeyEvent

enum class TvControllerKey {
  Up, Down, Left, Right, Enter, Back, Menu, Play
}

fun Int.toControllerKey(): TvControllerKey? = when (this) {
  KeyEvent.KEYCODE_DPAD_UP -> TvControllerKey.Up
  KeyEvent.KEYCODE_DPAD_DOWN -> TvControllerKey.Down
  KeyEvent.KEYCODE_DPAD_LEFT -> TvControllerKey.Left
  KeyEvent.KEYCODE_DPAD_RIGHT -> TvControllerKey.Right
  KeyEvent.KEYCODE_DPAD_CENTER,
  KeyEvent.KEYCODE_BUTTON_SELECT,
  KeyEvent.KEYCODE_ENTER,
  KeyEvent.KEYCODE_BUTTON_A -> TvControllerKey.Enter
  KeyEvent.KEYCODE_BACK -> TvControllerKey.Back
  KeyEvent.KEYCODE_MEDIA_PLAY -> TvControllerKey.Play
  else -> null
}