package com.seiko.compose.ncnnsr

object NativeLib {

  /**
   * A native method that is implemented by the 'ncnnsr' native library,
   * which is packaged with this application.
   */
  external fun stringFromJNI(): String

  // companion object {
    // Used to load the 'ncnnsr' library on application startup.
    init {
      System.loadLibrary("ncnnsr")
    }
  // }
}