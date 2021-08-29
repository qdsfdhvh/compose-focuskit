package com.seiko.compose.focuskit.demo

import android.app.Application
import android.util.Log
import com.seiko.compose.focuskit.TvLogger
import java.io.PrintWriter
import java.io.StringWriter

class DemoApp : Application() {
  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      TvLogger.setLogger(object : TvLogger {
        override var level: Int = Log.DEBUG
        override fun log(level: Int, msg: String?, throwable: Throwable?) {
          if (msg != null) {
            Log.println(level, "Focuskit", msg)
          }
          if (throwable != null) {
            val writer = StringWriter()
            throwable.printStackTrace(PrintWriter(writer))
            Log.println(level, "Focuskit", writer.toString())
          }
        }
      })
    }
  }
}