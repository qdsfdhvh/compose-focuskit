package com.seiko.compose.focuskit

import androidx.compose.foundation.lazy.LazyListState

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

  override val focusChangedDispatcher by lazy(LazyThreadSafetyMode.NONE) {
    FocusChangedDispatcher()
  }

  override val focusKeyHandlerDispatcher by lazy(LazyThreadSafetyMode.NONE) {
    FocusKeyHandlerDispatcher()
  }

  override var focusHandler = object : TvFocusHandler {
    override fun handleKey(key: TvControllerKey, rootItem: RootTvFocusItem): Boolean {
      return this@TvFocusItem.handleKey(key, rootItem)
    }
  }

  override fun toString(): String {
    return "TvFocusItem($key)"
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

  override fun toString(): String {
    return "ContainerTvFocusItem($key)"
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

      field.asSequence()
        .filterNot { prev -> value.any { prev.id == it.id } }
        .forEach { it.focusState = TvFocusState.None }

      field = value

      value.forEachIndexed { index, item ->
        item.focusState = if (index == value.lastIndex) {
          TvFocusState.Active
        } else {
          TvFocusState.ActiveParent
        }
      }
    }

  override fun toString(): String {
    return "RootTvFocusItem"
  }
}

fun RootTvFocusItem.refocus(): Boolean {
  if (!isFocusable) {
    Logger.w("[refocus] Focus ignored, root not focusable")
    return false
  }

  val newFocusPath = getFocusPath()
  val foundChild = newFocusPath.lastOrNull()
  val focusPathText = newFocusPath.joinToString(" -> ") { it.toString() }
  if (foundChild == null) {
    Logger.w("[refocus] Focus- !found , path: $focusPathText")
    return false
  }

  if (focusPath.isSameWith(newFocusPath)) {
    return false
  }

  Logger.d("[refocus] Focus path: $focusPathText")
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

private fun List<TvFocusItem>.isSameWith(other: List<TvFocusItem>): Boolean {
  if (size != other.size) return false
  return withIndex().all { it.value.id == other[it.index].id }
}

private val TvFocusItem.key: String
  get() = id.toString(16)