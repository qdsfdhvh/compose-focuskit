package com.seiko.compose.focuskit

import android.util.Log

internal typealias Logger = TvLogger

interface TvLogger {
  var level: Int
  fun log(level: Int, msg: String?, throwable: Throwable?)

  companion object : Logger {

    private var default: Logger? = null

    fun setLogger(logger: Logger) {
      default = logger
    }

    override var level: Int = default?.level ?: Log.DEBUG

    override fun log(level: Int, msg: String?, throwable: Throwable?) {
      default?.log(level, msg, throwable)
    }
  }
}

internal fun Logger.log(priority: Int, lazyMessage: () -> String) {
  if (level <= priority) {
    log(Log.DEBUG, lazyMessage(), null)
  }
}

internal fun Logger.log(throwable: Throwable) {
  if (level <= Log.ERROR) {
    log(Log.DEBUG, null, throwable)
  }
}