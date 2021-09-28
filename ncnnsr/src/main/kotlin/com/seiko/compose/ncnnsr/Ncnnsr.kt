package com.seiko.compose.ncnnsr

import android.graphics.Bitmap

object Ncnnsr {

  external fun init(param: ByteArray, bin: ByteArray): Boolean

  external fun detect(bitmap: Bitmap): FloatArray

  init {
    System.loadLibrary("ncnnsr")
  }
}