package com.seiko.compose.focuskit

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import com.seiko.compose.focuskit.internal.FocusChangedDispatcherImpl
import com.seiko.compose.focuskit.internal.FocusKeyHandlerDispatcherImpl
import com.seiko.compose.focuskit.internal.handleKey
import com.seiko.compose.focuskit.internal.onFocusChanged

open class TvFocusItem : FocusChangedDispatcherOwner, FocusKeyHandlerDispatcherOwner,
  TvFocusHandlerOwner {

  open val id: Long = System.currentTimeMillis()

  var focusState: TvFocusState = TvFocusState.None
    set(value) {
      if (field != value) {
        field = value
        onFocusChanged(value)
      }
    }

  override val focusChangedDispatcher: FocusChangedDispatcher by lazy(LazyThreadSafetyMode.NONE) {
    FocusChangedDispatcherImpl()
  }

  override val focusKeyHandlerDispatcher: FocusKeyHandlerDispatcher by lazy(LazyThreadSafetyMode.NONE) {
    FocusKeyHandlerDispatcherImpl()
  }

  override var focusHandler = object : TvFocusHandler {
    override fun handleKey(key: TvControllerKey, rootItem: RootTvFocusItem): Boolean {
      return this@TvFocusItem.handleKey(key, rootItem)
    }
  }

  override fun toString(): String {
    return "TvFocusItem($id)"
  }
}

open class ContainerTvFocusItem : TvFocusItem() {

  var focusIndex: Int = 0
  private var children = mutableListOf<TvFocusItem>()

  override var focusHandler = object : TvFocusHandler {
    override fun handleKey(key: TvControllerKey, rootItem: RootTvFocusItem): Boolean {
      return this@ContainerTvFocusItem.handleKey(key, rootItem)
    }

    override fun getFocus(): TvFocusItem? {
      return getChild(focusIndex)
    }
  }

  open fun addChild(child: TvFocusItem) {
    children.add(child)
  }

  open fun getChild(index: Int): TvFocusItem? {
    return children.getOrNull(index)
  }

  open fun getLastIndex(): Int? {
    return children.lastIndex
  }

  // 在lazyList中，item会重建；
  // 但现阶段搜寻焦点对focusItem位置和对象都比较敏感；
  // 暂时根据index来进行复用。
  open fun <T : TvFocusItem> getOrCreateChild(index: Int?, factory: () -> T): T {
    if (index != null) {
      val item = getChild(index)
      if (item != null) {
        @Suppress("UNCHECKED_CAST")
        return item as T
      }
    }
    return factory().apply {
      addChild(this)
    }
  }

  override fun toString(): String {
    return "TvContainer($id)"
  }

  internal var listState: LazyListState? = null
}

class RootTvFocusItem : ContainerTvFocusItem() {

  var isFocusable: Boolean = false
    set(value) {
      if (field != value) {
        field = value
        if (value) refocus() else {
          focusPath = emptyList()
        }
      }
    }

  var focusPath: List<TvFocusItem> = emptyList()
    internal set(value) {
      if (field.isSameWith(value)) {
        return
      }

      field.minus(value.toHashSet()).forEach {
        it.focusState = TvFocusState.None
      }

      field = value
      focusPathReversed = value.asReversed()

      value.forEachIndexed { index, item ->
        item.focusState = if (index == value.lastIndex) {
          TvFocusState.Active
        } else {
          TvFocusState.ActiveParent
        }
      }
    }

  internal var focusPathReversed: List<TvFocusItem> = emptyList()
    private set

  override fun toString(): String {
    return "TvRoot"
  }
}

fun RootTvFocusItem.refocus(): Boolean {
  if (!isFocusable) {
    Logger.log(Log.WARN) { "focus ignored, root not focusable" }
    return false
  }

  val newFocusPath = getFocusPath()
  val foundChild = newFocusPath.lastOrNull()
  val focusPathText = newFocusPath.joinToString(" -> ") { it.toString() }
  if (foundChild == null) {
    Logger.log(Log.WARN) { "focus !found, focusPath: $focusPathText" }
    return false
  }

  if (focusPath.isSameWith(newFocusPath)) {
    return false
  }

  Logger.log(Log.INFO) { "focusPath: $focusPathText" }
  focusPath = newFocusPath
  return true
}

fun RootTvFocusItem.getFocusPath(): List<TvFocusItem> {
  if (!isFocusable) return emptyList()

  val focusPath = mutableListOf<TvFocusItem>(this)

  var focused = focusHandler.getFocus()
  while (focused != null) {
    focusPath.add(focused)
    focused = focused.focusHandler.getFocus()
  }
  return focusPath
}

internal fun RootTvFocusItem.handleKey(key: TvControllerKey): Boolean {
  if (!isFocusable) return false

  focusPathReversed.forEach { item ->
    Logger.log(Log.DEBUG) { "handleKey($key) with $item" }
    if (item.focusHandler.handleKey(key, this)) {
      Logger.log(Log.DEBUG) { "consume($key) with $item" }
      return true
    }
  }
  return false
}

private fun List<TvFocusItem>.isSameWith(other: List<TvFocusItem>): Boolean {
  if (size != other.size) return false
  return withIndex().all { it.value.id == other[it.index].id }
}