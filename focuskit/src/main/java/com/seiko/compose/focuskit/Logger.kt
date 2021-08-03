package com.seiko.compose.focuskit

import android.util.Log

interface Logger {
  fun log(level: Int, msg: String)

  companion object : Logger {

    private var default: Logger? = null

    private val logger: Logger
      get() {
        if (default != null) {
          return default!!
        }

        default = object : Logger {
          override fun log(level: Int, msg: String) {
            Log.println(level, "Focuskit", msg)
          }
        }
        return default!!
      }

    fun setLogger(logger: Logger) {
      default = logger
    }

    override fun log(level: Int, msg: String) {
      logger.log(level, msg)
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