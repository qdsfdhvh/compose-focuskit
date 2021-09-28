package com.seiko.compose.focuskit.demo.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.core.graphics.drawable.toDrawable
import coil.annotation.ExperimentalCoilApi
import coil.intercept.Interceptor
import coil.request.ImageResult
import coil.request.SuccessResult
import com.seiko.compose.ncnnsr.Ncnnsr
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(ExperimentalCoilApi::class)
class NcnnsrInterceptor(private val context: Context) : Interceptor {

  private val isInitSuccess = AtomicBoolean(false)

  init {
    // synchronized(this) {
    //   val param = context.assets.open("MobileSR.param.bin").readBytes()
    //   val bin = context.assets.open("MobileSR.bin").readBytes()
    //   val success = Ncnnsr.init(param, bin)
    //   isInitSuccess.lazySet(success)
    // }
  }

  override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
    val response = chain.proceed(chain.request)
    if (!isInitSuccess.get()) {
      return response
    }

    if (response is SuccessResult
      && chain.request.data.toString().startsWith("http")
      && response.drawable is BitmapDrawable
    ) {
      Log.d("Ncnnsr", "拦截图片:" + chain.request.data.toString())
      val drawable = response.drawable as BitmapDrawable

      val rgba = drawable.bitmap.copy(Bitmap.Config.ARGB_8888, true)
      val width = rgba.width
      val height = rgba.height

      val input = Bitmap.createScaledBitmap(rgba, width, height, false)
      val result = synchronized(this) {
        Ncnnsr.detect(input)
      }

      val output = Bitmap.createScaledBitmap(rgba, result.size, result.size, false)
      val outputDrawable = output.toDrawable(context.resources)

      return SuccessResult(
        drawable = outputDrawable,
        request = response.request,
        metadata = response.metadata
      )
    }
    return response
  }
}