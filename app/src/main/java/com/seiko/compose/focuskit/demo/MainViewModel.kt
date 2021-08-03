package com.seiko.compose.focuskit.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seiko.compose.focuskit.demo.model.Anime
import kotlinx.coroutines.flow.*

typealias AnimeGroup = List<Pair<String, List<Anime>>>

class MainViewModel : ViewModel() {

  val tabList: Flow<List<String>> = flow {
    emit(
      listOf(
        "首页",
        "日本动漫",
        "国产动漫",
        "美国动漫",
        "动漫电影",
        "亲子动漫",
        "新番动漫",
        "剧场版",
        "OVA版",
        "真人版",
        "动漫专题",
        "最近更新",
      )
    )
  }

  val animeGroup: StateFlow<AnimeGroup> = flow {
    emit(
      listOf(
        Anime("TSUKIPRO THE ANIMATION 2", "https://ddcdn-img.acplay.net/anime/13696.jpg!client"),
        Anime("小林家的龙女仆S", "https://ddcdn-img.acplay.net/anime/14678.jpg!client"),
        Anime("阴晴不定大哥哥", "https://ddcdn-img.acplay.net/anime/15240.jpg!client"),
        Anime("我们的重制人生", "https://ddcdn-img.acplay.net/anime/15320.jpg!client"),
        Anime("开挂药师的休闲生活～异世界里开药店～", "https://ddcdn-img.acplay.net/anime/15339.jpg!client"),
        Anime("致不灭的你", "https://ddcdn-img.acplay.net/anime/15351.jpg!client"),
        Anime("Love Live! Superstar!!", "https://ddcdn-img.acplay.net/anime/15374.jpg!client"),
        Anime("关于我转生变成史莱姆这档事 第二季 2", "https://ddcdn-img.acplay.net/anime/15456.jpg!client"),
        Anime("魔法纪录 魔法少女小圆外传 第二季 -觉醒前夜-", "https://ddcdn-img.acplay.net/anime/15472.jpg!client"),
      )
    )
  }.map { animes ->
    List(5) { "今日更新" to animes }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Lazily,
    initialValue = emptyList()
  )
}