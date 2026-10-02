package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.AppInfo
import com.example.model.IconShape
import com.example.model.IconTheme
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingSurfaceElevated
import com.example.ui.theme.NothingWhite

val AVAILABLE_CUSTOM_GLYPHS = listOf(
  "app", "phone", "chat", "camera", "browser", "settings",
  "gallery", "music", "calculator", "clock", "notes", "recorder",
  "weather", "files", "maps", "mail", "video", "game",
  "terminal", "star", "flame", "lock", "globe", "shield",
  "bolt", "bulb", "compass", "heart", "wallet", "cart"
)

@Composable
fun MonochromeAppIcon(
  app: AppInfo,
  iconTheme: IconTheme = IconTheme.MONOCHROME,
  iconShape: IconShape = IconShape.CIRCLE,
  accentColor: Color = NothingRed,
  size: Dp = 50.dp,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  val composeShape: Shape = when (iconShape) {
    IconShape.CIRCLE -> CircleShape
    IconShape.SQUIRCLE -> RoundedCornerShape(size * 0.36f)
    IconShape.OCTAGON -> CutCornerShape(size * 0.28f)
    IconShape.ROUNDED_SQUARE -> RoundedCornerShape(size * 0.2f)
  }

  val (bgColor, iconColor, borderColor) = when (iconTheme) {
    IconTheme.MONOCHROME -> Triple(NothingSurfaceElevated, NothingWhite, NothingBorder)
    IconTheme.MONOCHROME_RED -> Triple(NothingSurfaceElevated, NothingWhite, NothingBorder)
    IconTheme.INVERTED -> Triple(NothingWhite, NothingBlack, Color(0xFFE0E0E0))
  }

  Box(
    modifier = modifier
      .size(size)
      .clip(composeShape)
      .background(bgColor)
      .border(1.dp, borderColor, composeShape),
    contentAlignment = Alignment.Center
  ) {
    val glyph = getKnownVectorGlyph(app.iconGlyph)
    if (glyph != null) {
      Icon(
        imageVector = glyph,
        contentDescription = app.label,
        tint = iconColor,
        modifier = Modifier.size(size * 0.52f)
      )
    } else {
      // Load installed app icon and convert to monochrome bitmap
      val monoBitmap = remember(app.packageName) {
        loadMonochromeBitmap(context, app.packageName, (size.value * 2).toInt())
      }
      if (monoBitmap != null) {
        Image(
          bitmap = monoBitmap.asImageBitmap(),
          contentDescription = app.label,
          modifier = Modifier
            .size(size * 0.58f)
            .clip(composeShape)
        )
      } else {
        Icon(
          imageVector = Icons.Default.Apps,
          contentDescription = app.label,
          tint = iconColor,
          modifier = Modifier.size(size * 0.52f)
        )
      }
    }

    // Nothing Accent Dot (Red or custom accent)
    if (iconTheme == IconTheme.MONOCHROME_RED) {
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .offset(x = (-3).dp, y = 3.dp)
          .size(5.dp)
          .clip(CircleShape)
          .background(accentColor)
      )
    }
  }
}

fun getKnownVectorGlyph(glyph: String): ImageVector? {
  return when (glyph) {
    "camera" -> Icons.Default.CameraAlt
    "phone" -> Icons.Default.Phone
    "chat" -> Icons.Default.ChatBubbleOutline
    "browser" -> Icons.Default.Language
    "settings" -> Icons.Default.Settings
    "gallery" -> Icons.Default.Image
    "music" -> Icons.Default.MusicNote
    "calculator" -> Icons.Default.Calculate
    "clock" -> Icons.Default.Schedule
    "notes" -> Icons.Default.Description
    "recorder" -> Icons.Default.GraphicEq
    "weather" -> Icons.Default.WbSunny
    "files" -> Icons.Default.Folder
    "maps" -> Icons.Default.Map
    "mail" -> Icons.Default.MailOutline
    "video" -> Icons.Default.PlayCircleOutline
    "game" -> Icons.Default.Gamepad
    "terminal" -> Icons.Default.Terminal
    "star" -> Icons.Default.Star
    "flame" -> Icons.Default.LocalFireDepartment
    "lock" -> Icons.Default.Lock
    "globe" -> Icons.Default.Public
    "shield" -> Icons.Default.Security
    "bolt" -> Icons.Default.Bolt
    "bulb" -> Icons.Default.Lightbulb
    "compass" -> Icons.Default.Explore
    "heart" -> Icons.Default.Favorite
    "wallet" -> Icons.Default.AccountBalanceWallet
    "cart" -> Icons.Default.ShoppingCart
    else -> null
  }
}

private fun loadMonochromeBitmap(context: Context, packageName: String, sizePx: Int): Bitmap? {
  return try {
    val pm = context.packageManager
    val drawable: Drawable = pm.getApplicationIcon(packageName)
    val width = if (sizePx > 0) sizePx else 108
    val height = if (sizePx > 0) sizePx else 108

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, width, height)
    drawable.draw(canvas)

    // Convert to high contrast monochrome
    val monoBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val monoCanvas = Canvas(monoBitmap)
    val paint = Paint()

    val cm = ColorMatrix().apply {
      setSaturation(0f)
      val contrast = 1.35f
      val translate = (-0.5f * contrast + 0.5f) * 255f
      val scaleMatrix = floatArrayOf(
        contrast, 0f, 0f, 0f, translate,
        0f, contrast, 0f, 0f, translate,
        0f, 0f, contrast, 0f, translate,
        0f, 0f, 0f, 1f, 0f
      )
      postConcat(ColorMatrix(scaleMatrix))
    }
    paint.colorFilter = ColorMatrixColorFilter(cm)
    monoCanvas.drawBitmap(bitmap, 0f, 0f, paint)

    monoBitmap
  } catch (e: Exception) {
    null
  }
}
