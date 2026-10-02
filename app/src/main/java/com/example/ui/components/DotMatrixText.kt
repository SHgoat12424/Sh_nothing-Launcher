package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LedActiveWhite
import com.example.ui.theme.LedInactiveDark

/**
 * 5x7 Dot Matrix Font Bitmaps for Nothing OS style LED rendering.
 * 7 rows of 5 dots (bit 4 is leftmost, bit 0 is rightmost).
 */
object DotMatrixFont {
  val DIGITS_AND_CHARS: Map<Char, IntArray> = mapOf(
    '0' to intArrayOf(
      0b01110,
      0b10001,
      0b10011,
      0b10101,
      0b11001,
      0b10001,
      0b01110
    ),
    '1' to intArrayOf(
      0b00100,
      0b01100,
      0b00100,
      0b00100,
      0b00100,
      0b00100,
      0b01110
    ),
    '2' to intArrayOf(
      0b01110,
      0b10001,
      0b00001,
      0b00110,
      0b01000,
      0b10000,
      0b11111
    ),
    '3' to intArrayOf(
      0b11110,
      0b00001,
      0b00001,
      0b01110,
      0b00001,
      0b00001,
      0b11110
    ),
    '4' to intArrayOf(
      0b00010,
      0b00110,
      0b01010,
      0b10010,
      0b11111,
      0b00010,
      0b00010
    ),
    '5' to intArrayOf(
      0b11111,
      0b10000,
      0b11110,
      0b00001,
      0b00001,
      0b10001,
      0b01110
    ),
    '6' to intArrayOf(
      0b00110,
      0b01000,
      0b10000,
      0b11110,
      0b10001,
      0b10001,
      0b01110
    ),
    '7' to intArrayOf(
      0b11111,
      0b00001,
      0b00010,
      0b00100,
      0b01000,
      0b01000,
      0b01000
    ),
    '8' to intArrayOf(
      0b01110,
      0b10001,
      0b10001,
      0b01110,
      0b10001,
      0b10001,
      0b01110
    ),
    '9' to intArrayOf(
      0b01110,
      0b10001,
      0b10001,
      0b01111,
      0b00001,
      0b00010,
      0b01100
    ),
    ':' to intArrayOf(
      0b00000,
      0b01100,
      0b01100,
      0b00000,
      0b01100,
      0b01100,
      0b00000
    ),
    '.' to intArrayOf(
      0b00000,
      0b00000,
      0b00000,
      0b00000,
      0b00000,
      0b01100,
      0b01100
    ),
    '-' to intArrayOf(
      0b00000,
      0b00000,
      0b00000,
      0b11111,
      0b00000,
      0b00000,
      0b00000
    ),
    '/' to intArrayOf(
      0b00001,
      0b00010,
      0b00010,
      0b00100,
      0b01000,
      0b01000,
      0b10000
    ),
    ' ' to intArrayOf(
      0b00000,
      0b00000,
      0b00000,
      0b00000,
      0b00000,
      0b00000,
      0b00000
    ),
    'A' to intArrayOf(0b01110, 0b10001, 0b10001, 0b11111, 0b10001, 0b10001, 0b10001),
    'B' to intArrayOf(0b11110, 0b10001, 0b10001, 0b11110, 0b10001, 0b10001, 0b11110),
    'C' to intArrayOf(0b01111, 0b10000, 0b10000, 0b10000, 0b10000, 0b10000, 0b01111),
    'D' to intArrayOf(0b11110, 0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b11110),
    'E' to intArrayOf(0b11111, 0b10000, 0b10000, 0b11110, 0b10000, 0b10000, 0b11111),
    'F' to intArrayOf(0b11111, 0b10000, 0b10000, 0b11110, 0b10000, 0b10000, 0b10000),
    'G' to intArrayOf(0b01111, 0b10000, 0b10000, 0b10011, 0b10001, 0b10001, 0b01111),
    'H' to intArrayOf(0b10001, 0b10001, 0b10001, 0b11111, 0b10001, 0b10001, 0b10001),
    'I' to intArrayOf(0b01110, 0b00100, 0b00100, 0b00100, 0b00100, 0b00100, 0b01110),
    'J' to intArrayOf(0b00001, 0b00001, 0b00001, 0b00001, 0b10001, 0b10001, 0b01110),
    'K' to intArrayOf(0b10001, 0b10010, 0b10100, 0b11000, 0b10100, 0b10010, 0b10001),
    'L' to intArrayOf(0b10000, 0b10000, 0b10000, 0b10000, 0b10000, 0b10000, 0b11111),
    'M' to intArrayOf(0b10001, 0b11011, 0b10101, 0b10101, 0b10001, 0b10001, 0b10001),
    'N' to intArrayOf(0b10001, 0b11001, 0b10101, 0b10011, 0b10001, 0b10001, 0b10001),
    'O' to intArrayOf(0b01110, 0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b01110),
    'P' to intArrayOf(0b11110, 0b10001, 0b10001, 0b11110, 0b10000, 0b10000, 0b10000),
    'R' to intArrayOf(0b11110, 0b10001, 0b10001, 0b11110, 0b10100, 0b10010, 0b10001),
    'S' to intArrayOf(0b01111, 0b10000, 0b10000, 0b01110, 0b00001, 0b00001, 0b11110),
    'T' to intArrayOf(0b11111, 0b00100, 0b00100, 0b00100, 0b00100, 0b00100, 0b00100),
    'U' to intArrayOf(0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b01110),
    'V' to intArrayOf(0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b01010, 0b00100),
    'W' to intArrayOf(0b10001, 0b10001, 0b10001, 0b10101, 0b10101, 0b11011, 0b10001),
    'Y' to intArrayOf(0b10001, 0b10001, 0b01010, 0b00100, 0b00100, 0b00100, 0b00100),
    '°' to intArrayOf(0b01100, 0b10010, 0b10010, 0b01100, 0b00000, 0b00000, 0b00000),
    '%' to intArrayOf(0b11001, 0b11010, 0b00100, 0b01000, 0b01011, 0b10011, 0b00000),
  )
}

/**
 * Single Dot Matrix Character Composable.
 */
@Composable
fun DotMatrixChar(
  char: Char,
  modifier: Modifier = Modifier,
  dotRadius: Dp = 3.dp,
  dotSpacing: Dp = 2.dp,
  activeColor: Color = LedActiveWhite,
  inactiveColor: Color = LedInactiveDark,
  showInactiveDots: Boolean = true
) {
  val rows = remember(char) {
    val upper = char.uppercaseChar()
    DotMatrixFont.DIGITS_AND_CHARS[upper] ?: DotMatrixFont.DIGITS_AND_CHARS[' ']!!
  }

  val totalWidth = (dotRadius * 2 * 5) + (dotSpacing * 4)
  val totalHeight = (dotRadius * 2 * 7) + (dotSpacing * 6)

  Canvas(
    modifier = modifier
      .width(totalWidth)
      .height(totalHeight)
  ) {
    val radiusPx = dotRadius.toPx()
    val spacingPx = dotSpacing.toPx()
    val step = (radiusPx * 2) + spacingPx

    for (row in 0 until 7) {
      val rowBits = rows[row]
      for (col in 0 until 5) {
        val isDotActive = ((rowBits shr (4 - col)) and 1) == 1
        val centerX = (col * step) + radiusPx
        val centerY = (row * step) + radiusPx

        if (isDotActive) {
          drawCircle(
            color = activeColor,
            radius = radiusPx,
            center = Offset(centerX, centerY)
          )
        } else if (showInactiveDots) {
          drawCircle(
            color = inactiveColor,
            radius = radiusPx * 0.75f,
            center = Offset(centerX, centerY)
          )
        }
      }
    }
  }
}

/**
 * Full Dot Matrix String renderer for Nothing OS Widgets and Headers.
 */
@Composable
fun DotMatrixText(
  text: String,
  modifier: Modifier = Modifier,
  dotRadius: Dp = 2.5.dp,
  dotSpacing: Dp = 1.5.dp,
  charSpacing: Dp = 6.dp,
  activeColor: Color = LedActiveWhite,
  inactiveColor: Color = LedInactiveDark,
  showInactiveDots: Boolean = true
) {
  Row(modifier = modifier) {
    text.forEachIndexed { index, c ->
      DotMatrixChar(
        char = c,
        dotRadius = dotRadius,
        dotSpacing = dotSpacing,
        activeColor = activeColor,
        inactiveColor = inactiveColor,
        showInactiveDots = showInactiveDots
      )
      if (index < text.length - 1) {
        Spacer(modifier = Modifier.width(charSpacing))
      }
    }
  }
}
