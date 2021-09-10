package com.seiko.compose.focuskit.demo.ui.foundation

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.seiko.compose.focuskit.demo.ui.theme.AnimeTvTheme
import com.seiko.compose.focuskit.demo.util.ToastUtils

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TvMovieInfo(
  modifier: Modifier = Modifier,
  title: String = "",
  cover: String = "",
  releaseTime: String = "",
  state: String = "",
  tags: List<String> = emptyList(),
  description: String = "",
) {
  val context = LocalContext.current

  val interactionSource = remember { MutableInteractionSource() }
  val isFocused by interactionSource.collectIsFocusedAsState()

  val (btnStarFocusRequester) = FocusRequester.createRefs()

  ConstraintLayout(
    modifier = modifier
      .focusable(interactionSource = interactionSource)
      .fillMaxSize()
  ) {
    val (
      titleRef, coverRef,
      rowRef,
      descriptionRef,
      btnStarPref,
    ) = createRefs()

    NetworkImage(
      modifier = Modifier
        .constrainAs(coverRef) {
          top.linkTo(parent.top, 20.dp)
          end.linkTo(parent.end, 50.dp)
        }
        .size(200.dp, 300.dp),
      data = cover,
    )

    Text(
      modifier = Modifier
        .constrainAs(titleRef) {
          top.linkTo(parent.top, 20.dp)
          start.linkTo(parent.start, 20.dp)
          // end.linkTo(coverRef.start, 20.dp)
        },
      text = title,
      style = MaterialTheme.typography.h3,
    )

    Row(
      modifier = Modifier
        .constrainAs(rowRef) {
          top.linkTo(titleRef.bottom, 20.dp)
          start.linkTo(titleRef.start)
        },
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = releaseTime,
        style = MaterialTheme.typography.body1
      )

      Spacer(modifier = Modifier.width(20.dp))

      Text(
        text = state,
        style = MaterialTheme.typography.body1,
      )

      Spacer(modifier = Modifier.width(20.dp))

      for (tag in tags) {
        Text(
          modifier = Modifier
            .widthIn(min = 60.dp)
            .border(1.dp, MaterialTheme.colors.onSurface, RoundedCornerShape(2.dp))
            .padding(2.dp),
          text = tag,
          style = MaterialTheme.typography.body1,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(10.dp))
      }
    }

    Text(
      modifier = Modifier
        .constrainAs(descriptionRef) {
          top.linkTo(rowRef.bottom, 20.dp)
          start.linkTo(titleRef.start)
          end.linkTo(coverRef.start, 50.dp)
          bottom.linkTo(btnStarPref.top, 20.dp)
          width = Dimension.fillToConstraints
          height = Dimension.fillToConstraints
        },
      text = description,
      style = MaterialTheme.typography.subtitle1,
      maxLines = 5,
    )

    FocusableButton(
      modifier = Modifier
        .constrainAs(btnStarPref) {
          start.linkTo(titleRef.start, 20.dp)
          bottom.linkTo(parent.bottom, 0.dp)
        }
        .focusRequester(btnStarFocusRequester),
      onClick = { ToastUtils.showToast(context, "收藏") }
    ) {
      val imagePainter = rememberVectorPainter(image = Icons.Filled.Star)
      Icon(imagePainter, contentDescription = null)
    }
  }

  LaunchedEffect(title, isFocused) {
    if (isFocused) {
      btnStarFocusRequester.requestFocus()
    }
  }
}

// @Composable
// fun TvMovieInfo(
//   modifier: Modifier = Modifier,
//   title: String = "",
//   cover: String = "",
//   releaseTime: String = "",
//   state: String = "",
//   tags: List<String> = emptyList(),
//   description: String = "",
// ){
//
// }

@Preview(widthDp = 1280, heightDp = 300)
@Composable
private fun TvMovieInfoPreview() {
  AnimeTvTheme {
    Surface(color = MaterialTheme.colors.background) {
      TvMovieInfo(
        title = "小林家的龙女仆 第二季",
        releaseTime = "2021-07",
        state = "更新至第5集",
        tags = listOf("搞笑", "奇幻", "百合", "治愈"),
        description = "《小林家的龙女仆 第二季》主人公小林是一位女性系统工程师，某天她喝酒喝的很醉兴冲冲的跑到了山上遇到了一头龙，酒醉之下的小林对着龙大倒苦水。听到龙说自己无家可归时，小林趁着酒意开玩笑的说“那你就来我家吧”，之后龙真的就跑到小林的家里，并且还变成了一位女仆——于是一位龙女仆和小林的同居生活就这样开始了……",
      )
    }
  }
}
