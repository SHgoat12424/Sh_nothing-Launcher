package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.BatteryManager
import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.model.LauncherWidget
import com.example.model.WidgetType
import com.example.ui.theme.LedActiveRed
import com.example.ui.theme.LedActiveWhite
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingSurface
import com.example.ui.theme.NothingSurfaceElevated
import com.example.ui.theme.NothingTextPrimary
import com.example.ui.theme.NothingTextSecondary
import com.example.ui.theme.NothingWhite
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun NothingWidgetContainer(
  widget: LauncherWidget,
  modifier: Modifier = Modifier,
  onLongClick: (() -> Unit)? = null,
  onAppLaunch: ((AppInfo) -> Unit)? = null,
  installedApps: List<AppInfo> = emptyList()
) {
  val shape = RoundedCornerShape(26.dp)

  Box(
    modifier = modifier
      .clip(shape)
      .background(NothingSurface)
      .border(1.dp, NothingBorder, shape)
      .padding(14.dp)
  ) {
    when (widget.type) {
      WidgetType.DIGITAL_CLOCK -> NothingDigitalClockWidget()
      WidgetType.ANALOG_CLOCK -> NothingAnalogClockWidget()
      WidgetType.WEATHER -> NothingWeatherWidget()
      WidgetType.QUICK_SETTINGS -> NothingQuickSettingsWidget()
      WidgetType.PEDOMETER -> NothingPedometerWidget()
      WidgetType.VOICE_RECORDER -> NothingVoiceRecorderWidget()
      WidgetType.BIG_FOLDER -> NothingBigFolderWidget(installedApps, onAppLaunch)
      WidgetType.GLANCE_BAR -> NothingGlanceWidget()
    }
  }
}

/**
 * 1. Iconic Nothing Dot-Matrix Digital Clock Widget
 */
@Composable
fun NothingDigitalClockWidget(modifier: Modifier = Modifier) {
  var currentTime by remember { mutableStateOf(Date()) }
  val context = LocalContext.current

  LaunchedEffect(Unit) {
    while (true) {
      currentTime = Date()
      delay(1000)
    }
  }

  val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
  val dateFormatter = remember { SimpleDateFormat("EEE, d MMM", Locale.getDefault()) }
  val timeString = timeFormatter.format(currentTime)
  val dateString = dateFormatter.format(currentTime).uppercase()

  val batteryLevel = remember {
    val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
    bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 84
  }

  Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = dateString,
        color = NothingTextSecondary,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.sp
      )

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.BatteryFull,
          contentDescription = "Battery",
          tint = if (batteryLevel <= 20) NothingRed else NothingTextSecondary,
          modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
          text = "$batteryLevel%",
          color = NothingTextSecondary,
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace
        )
      }
    }

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
      contentAlignment = Alignment.Center
    ) {
      DotMatrixText(
        text = timeString,
        dotRadius = 2.4.dp,
        dotSpacing = 1.4.dp,
        charSpacing = 4.dp,
        activeColor = LedActiveWhite
      )
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Alarm,
          contentDescription = "Alarm",
          tint = NothingRed,
          modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "07:00",
          color = NothingTextPrimary,
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace
        )
      }

      // Nothing Red Status Indicator
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(NothingRed)
      )
    }
  }
}

/**
 * 2. Minimalist Nothing Analog Clock with signature Red Dot / Second Hand
 */
@Composable
fun NothingAnalogClockWidget(modifier: Modifier = Modifier) {
  var calendar by remember { mutableStateOf(Calendar.getInstance()) }

  LaunchedEffect(Unit) {
    while (true) {
      calendar = Calendar.getInstance()
      delay(500)
    }
  }

  val seconds = calendar.get(Calendar.SECOND)
  val minutes = calendar.get(Calendar.MINUTE)
  val hours = calendar.get(Calendar.HOUR)
  val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2f, size.height / 2f)
      val radius = (size.minDimension / 2f) - 6.dp.toPx()

      // Outer Dial Ticks
      for (i in 0 until 12) {
        val angleRad = Math.toRadians((i * 30 - 90).toDouble())
        val isCardinal = i % 3 == 0
        val tickLength = if (isCardinal) 8.dp.toPx() else 4.dp.toPx()
        val startRadius = radius - tickLength
        val start = Offset(
          (center.x + startRadius * cos(angleRad)).toFloat(),
          (center.y + startRadius * sin(angleRad)).toFloat()
        )
        val end = Offset(
          (center.x + radius * cos(angleRad)).toFloat(),
          (center.y + radius * sin(angleRad)).toFloat()
        )
        drawLine(
          color = if (isCardinal) NothingWhite else Color(0x55FFFFFF),
          start = start,
          end = end,
          strokeWidth = if (isCardinal) 2.dp.toPx() else 1.dp.toPx(),
          cap = StrokeCap.Round
        )
      }

      // Hour Hand
      val hourAngle = Math.toRadians(((hours + minutes / 60f) * 30 - 90).toDouble())
      val hourLength = radius * 0.52f
      val hourEnd = Offset(
        (center.x + hourLength * cos(hourAngle)).toFloat(),
        (center.y + hourLength * sin(hourAngle)).toFloat()
      )
      drawLine(
        color = NothingWhite,
        start = center,
        end = hourEnd,
        strokeWidth = 3.5.dp.toPx(),
        cap = StrokeCap.Round
      )

      // Minute Hand
      val minAngle = Math.toRadians(((minutes + seconds / 60f) * 6 - 90).toDouble())
      val minLength = radius * 0.76f
      val minEnd = Offset(
        (center.x + minLength * cos(minAngle)).toFloat(),
        (center.y + minLength * sin(minAngle)).toFloat()
      )
      drawLine(
        color = NothingWhite,
        start = center,
        end = minEnd,
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round
      )

      // Signature Nothing Red Second Hand / Dot
      val secAngle = Math.toRadians((seconds * 6 - 90).toDouble())
      val secLength = radius * 0.82f
      val secDotCenter = Offset(
        (center.x + secLength * cos(secAngle)).toFloat(),
        (center.y + secLength * sin(secAngle)).toFloat()
      )
      drawLine(
        color = NothingRed,
        start = center,
        end = secDotCenter,
        strokeWidth = 1.dp.toPx(),
        cap = StrokeCap.Round
      )
      drawCircle(
        color = NothingRed,
        radius = 3.5.dp.toPx(),
        center = secDotCenter
      )

      // Center Pivot
      drawCircle(
        color = NothingSurfaceElevated,
        radius = 4.dp.toPx(),
        center = center
      )
      drawCircle(
        color = NothingWhite,
        radius = 2.dp.toPx(),
        center = center
      )
    }

    // Date Pill in Dial
    Box(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(end = 12.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(NothingSurfaceElevated)
        .border(1.dp, NothingBorder, RoundedCornerShape(4.dp))
        .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
      Text(
        text = String.format(Locale.getDefault(), "%02d", dayOfMonth),
        color = NothingTextPrimary,
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

/**
 * 3. Nothing Weather Widget
 */
@Composable
fun NothingWeatherWidget(modifier: Modifier = Modifier) {
  Row(
    modifier = modifier.fillMaxSize(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(verticalArrangement = Arrangement.Center) {
      Text(
        text = "LONDON",
        color = NothingTextSecondary,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.5.sp
      )
      Spacer(modifier = Modifier.height(2.dp))
      Row(verticalAlignment = Alignment.Bottom) {
        DotMatrixText(
          text = "24°",
          dotRadius = 2.2.dp,
          dotSpacing = 1.2.dp,
          charSpacing = 3.dp,
          activeColor = LedActiveWhite
        )
      }
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "CLEAR SKY",
        color = NothingTextPrimary,
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace
      )
    }

    // Weather Icon Glyph inside Circular Pill
    Box(
      modifier = Modifier
        .size(46.dp)
        .clip(CircleShape)
        .background(NothingSurfaceElevated)
        .border(1.dp, NothingBorder, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.WbSunny,
        contentDescription = "Sunny",
        tint = NothingWhite,
        modifier = Modifier.size(24.dp)
      )
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .offset(x = (-2).dp, y = (-2).dp)
          .size(5.dp)
          .clip(CircleShape)
          .background(NothingRed)
      )
    }
  }
}

/**
 * 4. Nothing Quick Settings Toggles Widget (Torch, Wi-Fi, Sound, Bluetooth)
 */
@Composable
fun NothingQuickSettingsWidget(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  var isTorchOn by remember { mutableStateOf(false) }
  var soundMode by remember { mutableIntStateOf(0) } // 0: Normal, 1: Vibrate, 2: Silent

  val audioManager = remember {
    context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
  }

  val cameraManager = remember {
    try {
      context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    } catch (e: Exception) {
      null
    }
  }

  Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = "QUICK TOGGLES",
      color = NothingTextSecondary,
      fontSize = 11.sp,
      fontFamily = FontFamily.Monospace,
      fontWeight = FontWeight.Medium,
      letterSpacing = 1.sp
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Toggle 1: Torch / Flashlight
      QuickToggleButton(
        isActive = isTorchOn,
        icon = if (isTorchOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
        label = "TORCH",
        tag = "torch_toggle",
        onClick = {
          isTorchOn = !isTorchOn
          try {
            cameraManager?.cameraIdList?.firstOrNull()?.let { id ->
              cameraManager.setTorchMode(id, isTorchOn)
            }
          } catch (e: Exception) {
            // Virtual/no camera torch
          }
        }
      )

      // Toggle 2: Wi-Fi
      QuickToggleButton(
        isActive = true,
        icon = Icons.Default.Wifi,
        label = "WI-FI",
        tag = "wifi_toggle",
        onClick = {
          val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
          }
          context.startActivity(intent)
        }
      )
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Toggle 3: Sound Mode
      QuickToggleButton(
        isActive = soundMode == 0,
        icon = if (soundMode == 0) Icons.Default.Notifications else Icons.Default.NotificationsOff,
        label = when (soundMode) {
          0 -> "RING"
          1 -> "VIBE"
          else -> "MUTE"
        },
        tag = "sound_toggle",
        onClick = {
          soundMode = (soundMode + 1) % 3
          try {
            audioManager?.ringerMode = when (soundMode) {
              0 -> AudioManager.RINGER_MODE_NORMAL
              1 -> AudioManager.RINGER_MODE_VIBRATE
              else -> AudioManager.RINGER_MODE_SILENT
            }
          } catch (e: Exception) {
            // Permission or security restriction
          }
        }
      )

      // Toggle 4: Bluetooth
      QuickToggleButton(
        isActive = false,
        icon = Icons.Default.Bluetooth,
        label = "BT",
        tag = "bluetooth_toggle",
        onClick = {
          val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
          }
          context.startActivity(intent)
        }
      )
    }
  }
}

@Composable
private fun QuickToggleButton(
  isActive: Boolean,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  tag: String,
  onClick: () -> Unit
) {
  val shape = RoundedCornerShape(16.dp)
  Box(
    modifier = Modifier
      .size(width = 66.dp, height = 52.dp)
      .clip(shape)
      .background(if (isActive) NothingWhite else NothingSurfaceElevated)
      .border(1.dp, if (isActive) NothingWhite else NothingBorder, shape)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(),
        onClick = onClick
      )
      .testTag(tag),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (isActive) NothingBlack else NothingWhite,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = label,
        color = if (isActive) NothingBlack else NothingTextSecondary,
        fontSize = 8.5.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

/**
 * 5. Nothing Pedometer & Screen Time Widget
 */
@Composable
fun NothingPedometerWidget(modifier: Modifier = Modifier) {
  Row(
    modifier = modifier.fillMaxSize(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = "ACTIVE STATS",
        color = NothingTextSecondary,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "6,842 STEPS",
        color = NothingTextPrimary,
        fontSize = 13.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "3h 45m SCREEN",
        color = NothingTextSecondary,
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace
      )
    }

    // Segmented concentric activity rings
    Canvas(modifier = Modifier.size(52.dp)) {
      val center = Offset(size.width / 2f, size.height / 2f)
      val outerRadius = (size.minDimension / 2f) - 4.dp.toPx()
      val innerRadius = outerRadius - 7.dp.toPx()

      // Track backgrounds
      drawCircle(
        color = Color(0x1FFFFFFF),
        radius = outerRadius,
        center = center,
        style = Stroke(width = 4.dp.toPx())
      )
      drawCircle(
        color = Color(0x1FFFFFFF),
        radius = innerRadius,
        center = center,
        style = Stroke(width = 4.dp.toPx())
      )

      // Outer ring: Steps progress (68%)
      drawArc(
        color = NothingWhite,
        startAngle = -90f,
        sweepAngle = 245f,
        useCenter = false,
        topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
        size = Size(outerRadius * 2, outerRadius * 2),
        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
      )

      // Inner ring: Screen time / goal (Nothing Red)
      drawArc(
        color = NothingRed,
        startAngle = -90f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
        size = Size(innerRadius * 2, innerRadius * 2),
        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
      )
    }
  }
}

/**
 * 6. Nothing Voice Recorder Widget with Live Waveform Dots
 */
@Composable
fun NothingVoiceRecorderWidget(modifier: Modifier = Modifier) {
  var isRecording by remember { mutableStateOf(false) }
  var secondsElapsed by remember { mutableIntStateOf(0) }

  LaunchedEffect(isRecording) {
    if (isRecording) {
      while (isRecording) {
        delay(1000)
        secondsElapsed++
      }
    } else {
      secondsElapsed = 0
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(700, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  Row(
    modifier = modifier.fillMaxSize(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = if (isRecording) "RECORDING" else "QUICK MEMO",
        color = if (isRecording) NothingRed else NothingTextSecondary,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(4.dp))
      val mins = secondsElapsed / 60
      val secs = secondsElapsed % 60
      Text(
        text = String.format(Locale.getDefault(), "%02d:%02d", mins, secs),
        color = NothingTextPrimary,
        fontSize = 15.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold
      )
    }

    // Animated waveform bars / dots
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
      val barHeights = if (isRecording) listOf(14.dp, 24.dp, 32.dp, 20.dp, 10.dp) else listOf(6.dp, 6.dp, 6.dp, 6.dp, 6.dp)
      barHeights.forEachIndexed { i, h ->
        val heightMultiplier = if (isRecording && (i % 2 == 0)) pulseScale else 1f
        Box(
          modifier = Modifier
            .width(3.dp)
            .height(h * heightMultiplier)
            .clip(CircleShape)
            .background(if (isRecording) NothingWhite else Color(0x33FFFFFF))
        )
      }
    }

    // Record Toggle Button
    Box(
      modifier = Modifier
        .size(46.dp)
        .clip(CircleShape)
        .background(if (isRecording) NothingRed else NothingSurfaceElevated)
        .border(1.dp, if (isRecording) NothingRed else NothingBorder, CircleShape)
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = ripple(),
          onClick = { isRecording = !isRecording }
        )
        .testTag("recorder_widget_btn"),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
        contentDescription = "Voice Memo",
        tint = NothingWhite,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

/**
 * 7. Nothing OS Oversized 2x2 Circular Folder with Direct-Launch App Bubbles!
 */
@Composable
fun NothingBigFolderWidget(
  installedApps: List<AppInfo>,
  onAppLaunch: ((AppInfo) -> Unit)?,
  modifier: Modifier = Modifier
) {
  val folderApps = remember(installedApps) {
    installedApps.take(4).ifEmpty {
      listOf(
        AppInfo("p1", "com.nothing.phone", "", "Phone", iconGlyph = "phone"),
        AppInfo("p2", "com.nothing.messages", "", "Chat", iconGlyph = "chat"),
        AppInfo("p3", "com.nothing.camera", "", "Camera", iconGlyph = "camera"),
        AppInfo("p4", "com.nothing.browser", "", "Web", iconGlyph = "browser")
      )
    }
  }

  Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "ESSENTIALS",
        color = NothingTextSecondary,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.sp
      )
      Box(
        modifier = Modifier
          .size(5.dp)
          .clip(CircleShape)
          .background(NothingRed)
      )
    }

    // 2x2 Mini Apps Grid
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      folderApps.take(2).forEach { app ->
        MiniBubbleApp(app = app, onAppLaunch = onAppLaunch)
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      folderApps.drop(2).take(2).forEach { app ->
        MiniBubbleApp(app = app, onAppLaunch = onAppLaunch)
      }
    }
  }
}

@Composable
private fun MiniBubbleApp(
  app: AppInfo,
  onAppLaunch: ((AppInfo) -> Unit)?
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(),
        onClick = { onAppLaunch?.invoke(app) }
      )
      .padding(4.dp)
  ) {
    MonochromeAppIcon(
      app = app,
      size = 38.dp
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = app.label,
      color = NothingTextPrimary,
      fontSize = 9.sp,
      fontFamily = FontFamily.Monospace,
      maxLines = 1
    )
  }
}

/**
 * 8. Nothing Glance Horizontal Pill
 */
@Composable
fun NothingGlanceWidget(modifier: Modifier = Modifier) {
  val date = remember {
    SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()).uppercase()
  }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(38.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(NothingRed)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = date,
        color = NothingTextPrimary,
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp
      )
    }

    Text(
      text = "ALL SYSTEMS GO",
      color = NothingTextSecondary,
      fontSize = 10.sp,
      fontFamily = FontFamily.Monospace,
      letterSpacing = 1.sp
    )
  }
}
