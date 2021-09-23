package com.seiko.compose.player

import android.content.Context
import android.media.AudioManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import org.videolan.libvlc.LibVLCFactory
import org.videolan.libvlc.MediaFactory
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.interfaces.ILibVLC
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.libvlc.util.VLCUtil

interface VlcVideoPlayerFactory {

  fun createPlayer(context: Context, source: VideoPlayerSource): MediaPlayer

  companion object : VlcVideoPlayerFactory {
    override fun createPlayer(context: Context, source: VideoPlayerSource): MediaPlayer {
      return MediaPlayer(getLibVLC(context)).apply {
        media = source.toMedia(context)
      }
    }
  }
}

val mediaFactory by lazy { MediaFactory() }

val libVLCFactory by lazy { LibVLCFactory() }

private var sLibVLC: ILibVLC? = null

fun getLibVLC(context: Context): ILibVLC {
  if (sLibVLC != null) {
    return sLibVLC!!
  }
  synchronized(Unit) {
    if (sLibVLC == null) {

      val audioManager = ContextCompat.getSystemService(context, AudioManager::class.java)
      val audioTrackSessionId = audioManager?.generateAudioSessionId() ?: 0

      sLibVLC = libVLCFactory.getFromOptions(context, mutableListOf(
        "--no-audio-time-stretch",
        "--avcodec-skiploopfilter",
        "" + getDeblocking(-1),
        "--avcodec-skip-frame",
        "0",
        "--avcodec-skip-idct",
        "0",
        "--subsdec-encoding",
        "",
        "--stats",
        "--android-display-chroma",
        "RV16",
        "--audio-resampler",
        "soxr",
        "--audiotrack-session-id=$audioTrackSessionId",
        "--freetype-background-opacity=0",
        "--sout-chromecast-conversion-quality=2",
        "--sout-keep",
        "--smb-force-v1",
        "--preferred-resolution=-1",
      ))
    }
  }
  return sLibVLC!!
}

fun VideoPlayerSource.toMedia(context: Context): IMedia {
  return when (this) {
    is VideoPlayerSource.Network -> {
      mediaFactory.getFromUri(getLibVLC(context), url.toUri())
    }
    is VideoPlayerSource.Raw -> {
      mediaFactory.getFromAssetFileDescriptor(
        getLibVLC(context), context.resources.openRawResourceFd(resId)
      )
    }
  }
}

private fun getDeblocking(deblocking: Int): Int {
  var ret = deblocking
  if (deblocking < 0) {
    /**
     * Set some reasonable deblocking defaults:
     *
     * Skip all (4) for armv6 and MIPS by default
     * Skip non-ref (1) for all armv7 more than 1.2 Ghz and more than 2 cores
     * Skip non-key (3) for all devices that don't meet anything above
     */
    val m = VLCUtil.getMachineSpecs() ?: return ret
    if (m.hasArmV6 && !m.hasArmV7 || m.hasMips)
      ret = 4
    else if (m.frequency >= 1200 && m.processors > 2)
      ret = 1
    else if (m.bogoMIPS >= 1200 && m.processors > 2) {
      ret = 1
      Log.d("VLCUtil", "Used bogoMIPS due to lack of frequency info")
    } else
      ret = 3
  } else if (deblocking > 4) { // sanity check
    ret = 3
  }
  return ret
}