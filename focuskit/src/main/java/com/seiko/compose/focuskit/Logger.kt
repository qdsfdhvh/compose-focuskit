package com.seiko.compose.focuskit

import android.util.Log

interface Logger {
  fun log(level: Int, msg: String)

  companion object : Logger {

    private var default: Logger? = null

    fun setLogger(logger: Logger) {
      default = logger
    }

    override fun log(level: Int, msg: String) {
      default?.log(level, msg)
    }
  }
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun Logger.d(msg: String) {
  log(Log.DEBUG, msg)
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun Logger.i(msg: String) {
  log(Log.INFO, msg)
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun Logger.w(msg: String) {
  log(Log.WARN, msg)
}