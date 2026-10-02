package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.WallpaperStyle
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingRed

@Composable
fun WallpaperBackground(
  wallpaperStyle: WallpaperStyle,
  wallpaperDim: Float = 0.35f,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "wpAnim")
  val noiseShift by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 20f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "noiseShift"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(NothingBlack)
  ) {
    when (wallpaperStyle) {
      WallpaperStyle.AMOLED_BLACK -> {
        // True Pitch Black
      }
      WallpaperStyle.NOTHING_ABSTRACT -> {
        Image(
          painter = painterResource(id = R.drawable.nothing_wallpaper),
          contentDescription = "Wallpaper",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
      }
      WallpaperStyle.DOT_GRID_MATRIX -> {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val dotSpacingPx = 24.dp.toPx()
          val dotRadiusPx = 1.1.dp.toPx()
          val cols = (size.width / dotSpacingPx).toInt()
          val rows = (size.height / dotSpacingPx).toInt()

          for (r in 0..rows) {
            for (c in 0..cols) {
              val x = c * dotSpacingPx + (dotSpacingPx / 2f)
              val y = r * dotSpacingPx + (dotSpacingPx / 2f)
              drawCircle(
                color = Color(0x1AFFFFFF),
                radius = dotRadiusPx,
                center = Offset(x, y)
              )
            }
          }
        }
      }
      WallpaperStyle.MINIMAL_LINES -> {
        Canvas(modifier = Modifier.fillMaxSize()) {
          drawCircle(
            color = Color(0x12FFFFFF),
            radius = size.width * 0.7f,
            center = Offset(size.width * 0.9f, size.height * 0.3f),
            style = Stroke(width = 1.dp.toPx())
          )
          drawCircle(
            color = Color(0x0EFFFFFF),
            radius = size.width * 1.1f,
            center = Offset(size.width * 0.1f, size.height * 0.75f),
            style = Stroke(width = 1.dp.toPx())
          )
          drawCircle(
            color = NothingRed,
            radius = 3.dp.toPx(),
            center = Offset(size.width * 0.85f, size.height * 0.22f)
          )
        }
      }
      WallpaperStyle.MATRIX_DIGITAL -> {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val spacing = 28.dp.toPx()
          val cols = (size.width / spacing).toInt()
          val rows = (size.height / spacing).toInt()
          for (r in 0..rows) {
            for (c in 0..cols) {
              val isLit = (c * 7 + r * 13 + noiseShift.toInt()) % 11 == 0
              val isRed = (c * 3 + r * 5) % 17 == 0
              val x = c * spacing + (spacing / 2f)
              val y = r * spacing + (spacing / 2f)
              drawCircle(
                color = if (isRed) NothingRed.copy(alpha = 0.6f) else if (isLit) Color(0x55FFFFFF) else Color(0x10FFFFFF),
                radius = if (isLit) 1.8.dp.toPx() else 1.dp.toPx(),
                center = Offset(x, y)
              )
            }
          }
        }
      }
      WallpaperStyle.RED_HORIZON -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  NothingBlack,
                  NothingBlack,
                  Color(0xFF140203),
                  Color(0xFF2A0306)
                )
              )
            )
        )
      }
    }

    // Dynamic Dimmer Scrim
    if (wallpaperDim > 0f) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = wallpaperDim))
      )
    }
  }
}
