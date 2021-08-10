package com.seiko.compose.focuskit

import android.os.Bundle
import android.util.Log
import androidx.compose.ui.focus.FocusRequester

private const val KEY_ITEM_ID = "tv-focus-item:item-id"
private const val KEY_FOCUS_INDEX = "tv-focus-item:focus-index"
private const val KEY_CHILDREN = "tv-focus-item:children"

open class TvFocusItem {

  var id: Long = System.currentTimeMillis()
    private set

  internal val focusRequester = FocusRequester()

  var parent: ContainerTvFocusItem? = null
    internal set

  var root: RootTvFocusItem? = null
    internal set

  fun requestFocus() = refocus()

  open fun saveState(): Bundle {
    val bundle = Bundle()
    bundle.putLong(KEY_ITEM_ID, id)
    return bundle
  }

  open fun restoreState(bundle: Bundle) {
    id = bundle.getLong(KEY_ITEM_ID)
  }

  override fun toString(): String = "TvFocusItem($id)"
}

open class ContainerTvFocusItem : TvFocusItem() {

  var focusIndex: Int = 0

  private var children = mutableListOf<TvFocusItem>()

  open fun addChild(child: TvFocusItem) {
    children.add(child)
    child.parent = this
    child.root = root
  }

  open fun getChild(index: Int): TvFocusItem? {
    return children.getOrNull(index)
  }

  open fun getLastIndex(): Int? {
    return children.lastIndex
  }

  open fun getFocus(): TvFocusItem? {
    return getChild(focusIndex)
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

  override fun saveState(): Bundle {
    val bundle = super.saveState()
    bundle.putInt(KEY_FOCUS_INDEX, focusIndex)
    bundle.putParcelableArrayList(KEY_CHILDREN, children.mapTo(ArrayList()) { it.saveState() })
    return bundle
  }

  override fun restoreState(bundle: Bundle) {
    super.restoreState(bundle)
    focusIndex = bundle.getInt(KEY_FOCUS_INDEX, 0)
    bundle.getParcelableArrayList<Bundle>(KEY_CHILDREN)!!
      .forEach { childBundle ->
        if (childBundle.containsKey(KEY_CHILDREN)) {
          ContainerTvFocusItem()
        } else {
          TvFocusItem()
        }.run {
          addChild(this)
          restoreState(childBundle)
        }
      }
  }

  override fun toString(): String = "TvContainer($id)"
}

class RootTvFocusItem : ContainerTvFocusItem() {

  init {
    parent = null
    root = this
  }

  var focusPath: List<TvFocusItem> = emptyList()
    internal set(value) {
      if (field isSameWith value) {
        return
      }
      field = value
      field.lastOrNull()?.focusRequester?.let {
        try {
          it.requestFocus()
        } catch (e: IllegalStateException) {
          Logger.log(e)
        }
      }
    }

  override fun toString(): String = "TvRoot"
}

fun TvFocusItem.refocus(force: Boolean = false): Boolean {
  val root = root
  if (root == null) {
    Logger.log(Log.WARN) { "focusItem not bind root" }
    return false
  }

  val newFocusPath = when (this) {
    is RootTvFocusItem -> listOf(this) + focusPathIn
    is ContainerTvFocusItem -> focusPathOut + focusPathIn
    else -> focusPathOut
  }
  if (!force && root.focusPath isSameWith newFocusPath) {
    return false
  }

  Logger.log(Log.INFO) { "focusPath: ${newFocusPath.joinToString(" -> ") { it.toString() }}" }
  root.focusPath = newFocusPath
  return true
}

private val ContainerTvFocusItem.focusPathIn: List<TvFocusItem>
  get() = mutableListOf<TvFocusItem>().apply {
    var focused = getFocus()
    while (focused != null) {
      add(focused)
      focused = if (focused is ContainerTvFocusItem) {
        focused.getFocus()
      } else null
    }
  }

private val TvFocusItem.focusPathOut: List<TvFocusItem>
  get() = mutableListOf(this).apply {
    var focused = parent
    while (focused != null) {
      add(0, focused)
      focused = focused.parent
    }
  }

private infix fun List<TvFocusItem>.isSameWith(other: List<TvFocusItem>): Boolean {
  if (size != other.size) return false
  return withIndex().all { it.value.id == other[it.index].id }
}