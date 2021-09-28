package com.seiko.compose.focuskit.demo

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import com.seiko.compose.focuskit.TvLogger
import com.seiko.compose.focuskit.demo.image.NcnnsrInterceptor
import java.io.PrintWriter
import java.io.StringWriter

class DemoApp : Application(), coil.ImageLoaderFactory {

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

  @OptIn(ExperimentalCoilApi::class)
  override fun newImageLoader(): ImageLoader {
    return ImageLoader.Builder(this)
      .availableMemoryPercentage(0.25)
      .crossfade(true)
      .componentRegistry {
        add(NcnnsrInterceptor(this@DemoApp))
      }
      .build()
  }
}
