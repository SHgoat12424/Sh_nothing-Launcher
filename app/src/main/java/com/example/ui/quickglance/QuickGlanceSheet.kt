package com.example.ui.quickglance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DotMatrixText
import com.example.ui.theme.LedActiveWhite
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingDarkBackground
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingSurface
import com.example.ui.theme.NothingSurfaceElevated
import com.example.ui.theme.NothingTextPrimary
import com.example.ui.theme.NothingTextSecondary
import com.example.ui.theme.NothingWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickGlanceBottomSheet(
  onDismiss: () -> Unit,
  onOpenSettings: () -> Unit
) {
  var isPlayingMusic by remember { mutableStateOf(true) }
  var brightness by remember { mutableFloatStateOf(0.7f) }
  var dndActive by remember { mutableStateOf(false) }
  var hotspotActive by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    containerColor = NothingDarkBackground,
    contentColor = NothingWhite,
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(top = 10.dp, bottom = 6.dp)
          .width(36.dp)
          .height(4.dp)
          .clip(CircleShape)
          .background(NothingBorder)
      )
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 6.dp)
        .navigationBarsPadding(),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
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
            text = "QUICK GLANCE // SHELF",
            color = NothingWhite,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "Collapse",
            tint = NothingTextSecondary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // 1. Music Player Card (Nothing Tape motif)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(NothingSurface)
          .border(1.dp, NothingBorder, RoundedCornerShape(20.dp))
          .padding(14.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(NothingSurfaceElevated)
                  .border(1.dp, NothingBorder, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.GraphicEq,
                  contentDescription = "Audio",
                  tint = if (isPlayingMusic) NothingRed else NothingTextSecondary,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "NOTHING (1) AUDIO",
                  color = NothingWhite,
                  fontSize = 13.sp,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "NOW PLAYING // SYNTHESIS",
                  color = NothingTextSecondary,
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace
                )
              }
            }

            // Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                Icon(
                  imageVector = Icons.Default.FastRewind,
                  contentDescription = "Prev",
                  tint = NothingWhite,
                  modifier = Modifier.size(18.dp)
                )
              }
              IconButton(
                onClick = { isPlayingMusic = !isPlayingMusic },
                modifier = Modifier
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(NothingWhite)
              ) {
                Icon(
                  imageVector = if (isPlayingMusic) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = "Play/Pause",
                  tint = NothingBlack,
                  modifier = Modifier.size(18.dp)
                )
              }
              IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                Icon(
                  imageVector = Icons.Default.FastForward,
                  contentDescription = "Next",
                  tint = NothingWhite,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }
      }

      // 2. Brightness Slider Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(NothingSurface)
          .border(1.dp, NothingBorder, RoundedCornerShape(16.dp))
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(
            imageVector = Icons.Default.BrightnessMedium,
            contentDescription = "Brightness",
            tint = NothingWhite,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Slider(
            value = brightness,
            onValueChange = { brightness = it },
            colors = SliderDefaults.colors(
              thumbColor = NothingWhite,
              activeTrackColor = NothingWhite,
              inactiveTrackColor = NothingBorder
            ),
            modifier = Modifier.weight(1f)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "${(brightness * 100).toInt()}%",
            color = NothingTextPrimary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // 3. Quick Toggles Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        GlancePill(
          icon = Icons.Default.DoNotDisturbOn,
          label = "DND",
          isActive = dndActive,
          onClick = { dndActive = !dndActive },
          modifier = Modifier.weight(1f)
        )
        GlancePill(
          icon = Icons.Default.WifiTethering,
          label = "HOTSPOT",
          isActive = hotspotActive,
          onClick = { hotspotActive = !hotspotActive },
          modifier = Modifier.weight(1f)
        )
        GlancePill(
          icon = Icons.Default.NightlightRound,
          label = "DARK",
          isActive = true,
          onClick = {},
          modifier = Modifier.weight(1f)
        )
      }

      // 4. Notifications Glance Preview
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(NothingSurface)
          .border(1.dp, NothingBorder, RoundedCornerShape(18.dp))
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "RECENT GLANCE (2)",
            color = NothingTextSecondary,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
          )
          Text(
            text = "CLEAR ALL",
            color = NothingRed,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        GlanceNotificationItem(
          app = "SYSTEM",
          time = "5m",
          title = "Nothing Launcher Active",
          body = "Ultra-fast monochrome setup running at 120 FPS."
        )

        HorizontalDivider(color = NothingBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))

        GlanceNotificationItem(
          app = "CALENDAR",
          time = "1h",
          title = "Weekly Architecture Review",
          body = "11:00 AM - Design Team Meetup"
        )
      }

      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}

@Composable
private fun GlancePill(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  isActive: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(16.dp)
  Box(
    modifier = modifier
      .height(50.dp)
      .clip(shape)
      .background(if (isActive) NothingWhite else NothingSurface)
      .border(1.dp, if (isActive) NothingWhite else NothingBorder, shape)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(),
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (isActive) NothingBlack else NothingWhite,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = label,
        color = if (isActive) NothingBlack else NothingTextSecondary,
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
private fun GlanceNotificationItem(
  app: String,
  time: String,
  title: String,
  body: String
) {
  Column {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = app,
        color = NothingRed,
        fontSize = 9.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = time,
        color = NothingTextSecondary,
        fontSize = 9.sp,
        fontFamily = FontFamily.Monospace
      )
    }
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = title,
      color = NothingWhite,
      fontSize = 12.sp,
      fontFamily = FontFamily.Monospace,
      fontWeight = FontWeight.Medium
    )
    Text(
      text = body,
      color = NothingTextSecondary,
      fontSize = 10.sp,
      fontFamily = FontFamily.Monospace
    )
  }
}
