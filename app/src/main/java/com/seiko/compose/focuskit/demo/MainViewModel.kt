package com.seiko.compose.focuskit.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seiko.compose.focuskit.demo.model.Anime
import com.seiko.compose.focuskit.demo.model.AnimeDetail
import com.seiko.compose.focuskit.demo.model.AnimeEpisode
import com.seiko.compose.player.VideoPlayerSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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
        Anime(
          "斗罗大陆",
          "http://css.njhzmxx.com/acg/2021/07/17/20210717112154268.jpg",
          "/show/4066.html"
        ),
        Anime(
          "桃子男孩渡海而来",
          "http://css.njhzmxx.com/down/1/808165613360701.jpg",
          "/show/5037.html",
        ),
        Anime(
          "女友成双",
          "http://css.njhzmxx.com/acg/2021/03/28/20210328084624549.jpg",
          "/show/5224.html",
        ),
        Anime(
          "BLUE REFLECTION RAY / 澪",
          "http://css.njhzmxx.com/acg/2021/02/18/20210218061311802.jpg",
          "/show/5190.html",
        ),
        Anime(
          "转生恶役只好拔除破灭旗标 第二季",
          "http://css.njhzmxx.com/acg/2020/06/21/20200621095007415.jpg",
          "/show/5009.html",
        ),
        Anime(
          "瓦尼塔斯的笔记",
          "http://css.njhzmxx.com/acg/2021/03/30/20210330083355930.jpg",
          "/show/5229.html",
        ),
        Anime(
          "我立于百万生命之上 第二季",
          "http://css.njhzmxx.com/acg/2021/07/02/20210702110715724.jpg",
          "/show/5295.html",
        ),
        Anime(
          "异世界迷宫黑心企业",
          "http://css.njhzmxx.com/acg/2021/03/26/20210326053709364.jpg",
          "/show/5220.html",
        ),
        Anime(
          "白沙的水族馆",
          "http://css.njhzmxx.com/acg/2021/07/08/20210708111909554.jpg",
          "/show/5311.html",
        ),
      )
    )
  }.map { animes ->
    List(10) { "今日更新$it" to (animes + animes) }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Lazily,
    initialValue = emptyList()
  )

  val animeDetail: StateFlow<AnimeDetail> = flow {
    emit(
      AnimeDetail(
        title = "我们的重制人生",
        cover = "http://css.njhzmxx.com/acg/2021/03/07/20210307081157597.jpg",
        alias = "更新至6集",
        rating = 5.0f,
        releaseTime = "2021-07",
        area = "日本",
        types = listOf("校园", "恋爱"),
        indexes = listOf("W动漫"),
        tags = listOf("日语", "tv"),
        state = "更新至6集",
        description = "《我们的重制人生》电视动画《我们的重制人生》改编自木绪なち原作、えれっと负责插画的同名轻小说作品，于2019年12月20日宣布了动画化企划进行中的消息。该片由feel 负责制作，于2021年7月起播出 [ 突然醒来后，我发现自己回到了10年前的今天。 我，桥场恭也是前途黯淡途的游戏导演。公司破产，企划也夭折了，于是回到了老家…… 不想再看到明星创作者们的亮眼表现，我闷闷不乐地睡着之后再醒来，发现自己不知为什么回到了十年前大学入学的时刻！？ 考上了原本应该落榜的大学，迎接向往的艺大生活，甚至还过着男女四人一起分租的同居生活，玫瑰色的每一天就此展开！我的人生道路将从这里开始重新塑造—— 与未来将会超有名的创作者一起度过的新生活就要开始了！ 尽管如此自信满满地跨出第一步，事情却似乎没有那么顺利…",
        episodeList = listOf(
          AnimeEpisode("第06级", "/v/5207-6.html"),
          AnimeEpisode("第05级", "/v/5207-5.html"),
          AnimeEpisode("第04级", "/v/5207-4.html"),
          AnimeEpisode("第03级", "/v/5207-3.html"),
          AnimeEpisode("第02级", "/v/5207-2.html"),
          AnimeEpisode("第01级", "/v/5207-1.html"),
          AnimeEpisode("PV2", "/v/5207-pv2.html"),
          AnimeEpisode("PV1", "/v/5207-pv1.html"),
        ),
        relatedList = listOf(
          Anime(
            "斗罗大陆",
            "http://css.njhzmxx.com/acg/2021/07/17/20210717112154268.jpg",
            "/show/4066.html"
          ),
          Anime(
            "桃子男孩渡海而来",
            "http://css.njhzmxx.com/down/1/808165613360701.jpg",
            "/show/5037.html",
          ),
          Anime(
            "女友成双",
            "http://css.njhzmxx.com/acg/2021/03/28/20210328084624549.jpg",
            "/show/5224.html",
          ),
          Anime(
            "BLUE REFLECTION RAY / 澪",
            "http://css.njhzmxx.com/acg/2021/02/18/20210218061311802.jpg",
            "/show/5190.html",
          ),
          Anime(
            "转生恶役只好拔除破灭旗标 第二季",
            "http://css.njhzmxx.com/acg/2020/06/21/20200621095007415.jpg",
            "/show/5009.html",
          ),
          Anime(
            "瓦尼塔斯的笔记",
            "http://css.njhzmxx.com/acg/2021/03/30/20210330083355930.jpg",
            "/show/5229.html",
          ),
          Anime(
            "我立于百万生命之上 第二季",
            "http://css.njhzmxx.com/acg/2021/07/02/20210702110715724.jpg",
            "/show/5295.html",
          ),
          Anime(
            "异世界迷宫黑心企业",
            "http://css.njhzmxx.com/acg/2021/03/26/20210326053709364.jpg",
            "/show/5220.html",
          ),
          Anime(
            "白沙的水族馆",
            "http://css.njhzmxx.com/acg/2021/07/08/20210708111909554.jpg",
            "/show/5311.html",
          ),
        )
      )
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Lazily,
    initialValue = AnimeDetail()
  )

  val animePlayer = flow {
    emit(
      VideoPlayerSource.Network(
        url = "https://v11.tkzyapi.com/20210827/90uHeEUi/index.m3u8"
      )
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Lazily,
    initialValue = null
  )
}
