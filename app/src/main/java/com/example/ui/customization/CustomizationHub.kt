package com.example.ui.customization

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dock
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccentMode
import com.example.model.AppInfo
import com.example.model.DockStyle
import com.example.model.IconShape
import com.example.model.IconTheme
import com.example.model.LauncherSettings
import com.example.model.LauncherWidget
import com.example.model.WallpaperStyle
import com.example.model.WidgetType
import com.example.ui.components.AVAILABLE_CUSTOM_GLYPHS
import com.example.ui.components.MonochromeAppIcon
import com.example.ui.components.getKnownVectorGlyph
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
fun CustomizationBottomSheet(
  currentSettings: LauncherSettings,
  activeWidgets: List<LauncherWidget>,
  installedApps: List<AppInfo> = emptyList(),
  onUpdateSettings: (LauncherSettings) -> Unit,
  onAddWidget: (LauncherWidget) -> Unit,
  onRemoveWidget: (String) -> Unit,
  onResetWidgets: () -> Unit,
  onUpdateCustomGlyph: ((appId: String, glyph: String) -> Unit)? = null,
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: DOCK, 1: ICONS, 2: WALLPAPER, 3: WIDGETS, 4: NICHE / GLYPH

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    containerColor = NothingSurface,
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
        .padding(horizontal = 18.dp)
        .navigationBarsPadding()
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(currentSettings.accentMode.hexColor))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "NOTHING OS STUDIO",
              color = NothingWhite,
              fontSize = 15.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          }
          Text(
            text = "CUSTOMIZE DOCK, ICONS, WALLPAPERS & GLYPH",
            color = NothingTextSecondary,
            fontSize = 9.5.sp,
            fontFamily = FontFamily.Monospace
          )
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(NothingSurfaceElevated)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = NothingWhite,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Tab selector row
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        item { CustomTabPill("DOCK", isSelected = selectedTab == 0) { selectedTab = 0 } }
        item { CustomTabPill("ICONS", isSelected = selectedTab == 1) { selectedTab = 1 } }
        item { CustomTabPill("WALLPAPER", isSelected = selectedTab == 2) { selectedTab = 2 } }
        item { CustomTabPill("WIDGETS", isSelected = selectedTab == 3) { selectedTab = 3 } }
        item { CustomTabPill("NICHE / GLYPH", isSelected = selectedTab == 4) { selectedTab = 4 } }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Tab Content
      when (selectedTab) {
        0 -> DockCustomizationTab(
          settings = currentSettings,
          installedApps = installedApps,
          onUpdateSettings = onUpdateSettings
        )
        1 -> IconsCustomizationTab(
          settings = currentSettings,
          installedApps = installedApps,
          onUpdateSettings = onUpdateSettings,
          onUpdateCustomGlyph = onUpdateCustomGlyph
        )
        2 -> WallpaperCustomizationTab(
          settings = currentSettings,
          onUpdateSettings = onUpdateSettings
        )
        3 -> WidgetsCustomizationTab(
          activeWidgets = activeWidgets,
          onAddWidget = onAddWidget,
          onRemoveWidget = onRemoveWidget,
          onResetWidgets = onResetWidgets
        )
        4 -> NicheAndGlyphTab(
          settings = currentSettings,
          onUpdateSettings = onUpdateSettings
        )
      }

      Spacer(modifier = Modifier.height(14.dp))
    }
  }
}

@Composable
private fun CustomTabPill(title: String, isSelected: Boolean, onClick: () -> Unit) {
  val shape = RoundedCornerShape(12.dp)
  Box(
    modifier = Modifier
      .clip(shape)
      .background(if (isSelected) NothingWhite else NothingSurfaceElevated)
      .border(1.dp, if (isSelected) NothingWhite else NothingBorder, shape)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(),
        onClick = onClick
      )
      .padding(horizontal = 12.dp, vertical = 6.dp)
  ) {
    Text(
      text = title,
      color = if (isSelected) NothingBlack else NothingTextSecondary,
      fontSize = 10.sp,
      fontFamily = FontFamily.Monospace,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
      letterSpacing = 0.5.sp
    )
  }
}

/**
 * 1. DOCK CUSTOMIZATION TAB
 */
@Composable
private fun DockCustomizationTab(
  settings: LauncherSettings,
  installedApps: List<AppInfo>,
  onUpdateSettings: (LauncherSettings) -> Unit
) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    item {
      Text(
        text = "DOCK STYLE & CONTAINER",
        color = NothingTextSecondary,
        fontSize = 10.5.sp,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        DockStyle.values().forEach { style ->
          val isSelected = settings.dockStyle == style
          val shape = RoundedCornerShape(12.dp)
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(shape)
              .background(if (isSelected) NothingSurfaceElevated else NothingDarkBackground)
              .border(1.dp, if (isSelected) NothingWhite else NothingBorder, shape)
              .clickable { onUpdateSettings(settings.copy(dockStyle = style)) }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = style.label.uppercase(),
              color = if (isSelected) NothingWhite else NothingTextSecondary,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }
    }

    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(NothingSurfaceElevated)
          .border(1.dp, NothingBorder, RoundedCornerShape(14.dp))
          .padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
        Column {
          Text(
            text = "DOCK APP COUNT",
            color = NothingWhite,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "${settings.dockAppCount} apps in bottom dock",
            color = NothingTextSecondary,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          listOf(3, 4, 5).forEach { count ->
            val isSelected = settings.dockAppCount == count
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isSelected) NothingWhite else NothingSurface)
                .border(1.dp, if (isSelected) NothingWhite else NothingBorder, CircleShape)
                .clickable { onUpdateSettings(settings.copy(dockAppCount = count)) },
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = count.toString(),
                color = if (isSelected) NothingBlack else NothingTextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    item {
      Text(
        text = "PINNED DOCK APPS (SELECT UP TO ${settings.dockAppCount})",
        color = NothingTextSecondary,
        fontSize = 10.5.sp,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )
    }

    // Grid of apps to toggle dock pinning
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
      ) {
        items(installedApps.take(16)) { app ->
          val isDocked = app.isPinnedHome
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(if (isDocked) NothingSurfaceElevated else Color.Transparent)
              .border(1.dp, if (isDocked) NothingRed else NothingBorder, RoundedCornerShape(14.dp))
              .clickable {
                // Pin/unpin from dock
              }
              .padding(8.dp)
          ) {
            MonochromeAppIcon(app = app, size = 38.dp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = app.label,
              color = if (isDocked) NothingWhite else NothingTextSecondary,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace,
              maxLines = 1
            )
          }
        }
      }
    }
  }
}

/**
 * 2. ICONS CUSTOMIZATION TAB
 */
@Composable
private fun IconsCustomizationTab(
  settings: LauncherSettings,
  installedApps: List<AppInfo>,
  onUpdateSettings: (LauncherSettings) -> Unit,
  onUpdateCustomGlyph: ((appId: String, glyph: String) -> Unit)?
) {
  var selectedAppForGlyph by remember { mutableStateOf<AppInfo?>(null) }

  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    // Icon Shape
    item {
      Text(
        text = "ICON SHAPE",
        color = NothingTextSecondary,
        fontSize = 10.5.sp,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        IconShape.values().forEach { shape ->
          val isSelected = settings.iconShape == shape
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) NothingSurfaceElevated else NothingDarkBackground)
              .border(1.dp, if (isSelected) NothingWhite else NothingBorder, RoundedCornerShape(12.dp))
              .clickable { onUpdateSettings(settings.copy(iconShape = shape)) }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = shape.label.uppercase(),
              color = if (isSelected) NothingWhite else NothingTextSecondary,
              fontSize = 9.5.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }
    }

    // Icon Size Scaling
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(NothingSurfaceElevated)
          .border(1.dp, NothingBorder, RoundedCornerShape(14.dp))
          .padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
          Text(
            text = "ICON SCALE // COMPACT DENSITY",
            color = NothingWhite,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = when {
              settings.iconScale < 0.85f -> "Ultra Compact (38dp)"
              settings.iconScale < 0.95f -> "Compact (44dp)"
              else -> "Standard (50dp)"
            },
            color = NothingTextSecondary,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
          )
          Slider(
            value = settings.iconScale,
            onValueChange = { onUpdateSettings(settings.copy(iconScale = it)) },
            valueRange = 0.75f..1.1f,
            colors = SliderDefaults.colors(
              thumbColor = NothingWhite,
              activeTrackColor = NothingWhite,
              inactiveTrackColor = NothingBorder
            )
          )
        }
      }
    }

    // Show App Labels Toggle (Icon-only purist mode)
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(NothingSurfaceElevated)
          .border(1.dp, NothingBorder, RoundedCornerShape(14.dp))
          .padding(horizontal = 14.dp, vertical = 8.dp)
      ) {
        Column {
          Text(
            text = "SHOW APP LABELS",
            color = NothingWhite,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = if (settings.showAppLabels) "Names displayed under icons" else "Minimal icon-only purist mode",
            color = NothingTextSecondary,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
          )
        }
        Switch(
          checked = settings.showAppLabels,
          onCheckedChange = { onUpdateSettings(settings.copy(showAppLabels = it)) },
          colors = SwitchDefaults.colors(
            checkedThumbColor = NothingWhite,
            checkedTrackColor = NothingRed,
            uncheckedThumbColor = NothingTextSecondary,
            uncheckedTrackColor = NothingDarkBackground,
            uncheckedBorderColor = NothingBorder
          )
        )
      }
    }

    // Custom Icon Glyph Assignment (Change any app's glyph!)
    item {
      Text(
        text = "CUSTOM GLYPH ASSIGNMENT",
        color = NothingTextSecondary,
        fontSize = 10.5.sp,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )
    }

    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(NothingSurfaceElevated)
          .border(1.dp, NothingBorder, RoundedCornerShape(14.dp))
          .padding(12.dp)
      ) {
        Text(
          text = "Tap any app to customize its Nothing vector glyph:",
          color = NothingTextSecondary,
          fontSize = 10.sp,
          fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(installedApps.take(12)) { app ->
            val isSelected = selectedAppForGlyph?.id == app.id
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) NothingWhite else NothingSurface)
                .border(1.dp, if (isSelected) NothingWhite else NothingBorder, RoundedCornerShape(10.dp))
                .clickable { selectedAppForGlyph = app }
                .padding(6.dp)
            ) {
              Text(
                text = app.label,
                color = if (isSelected) NothingBlack else NothingWhite,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }

        // Glyph Picker
        selectedAppForGlyph?.let { app ->
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Select glyph for ${app.label}:",
            color = NothingWhite,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))

          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(AVAILABLE_CUSTOM_GLYPHS) { glyphName ->
              val iconVector = getKnownVectorGlyph(glyphName)
              if (iconVector != null) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (app.iconGlyph == glyphName) NothingRed else NothingSurface)
                    .border(1.dp, NothingBorder, CircleShape)
                    .clickable {
                      onUpdateCustomGlyph?.invoke(app.id, glyphName)
                    },
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = iconVector,
                    contentDescription = glyphName,
                    tint = NothingWhite,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * 3. WALLPAPER CUSTOMIZATION TAB
 */
@Composable
private fun WallpaperCustomizationTab(
  settings: LauncherSettings,
  onUpdateSettings: (LauncherSettings) -> Unit
) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    item {
      Text(
        text = "WALLPAPER GALLERY",
        color = NothingTextSecondary,
        fontSize = 10.5.sp,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )
    }

    items(WallpaperStyle.values()) { wp ->
      val isSelected = settings.wallpaperStyle == wp
      val shape = RoundedCornerShape(14.dp)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .clip(shape)
          .background(if (isSelected) NothingSurfaceElevated else NothingDarkBackground)
          .border(1.dp, if (isSelected) NothingWhite else NothingBorder, shape)
          .clickable { onUpdateSettings(settings.copy(wallpaperStyle = wp)) }
          .padding(horizontal = 14.dp, vertical = 12.dp)
      ) {
        Column {
          Text(
            text = wp.label,
            color = NothingWhite,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = when (wp) {
              WallpaperStyle.AMOLED_BLACK -> "Zero pixel power, maximum OLED black"
              WallpaperStyle.NOTHING_ABSTRACT -> "Transparent mechanical glass ribbon artwork"
              WallpaperStyle.DOT_GRID_MATRIX -> "Subtle hardware LED dot-matrix grid"
              WallpaperStyle.MINIMAL_LINES -> "Precision architectural curves with Nothing red accent"
              WallpaperStyle.MATRIX_DIGITAL -> "Cybernetic digital noise particle flow"
              WallpaperStyle.RED_HORIZON -> "Deep noir with blood red atmospheric gradient"
            },
            color = NothingTextSecondary,
            fontSize = 9.5.sp,
            fontFamily = FontFamily.Monospace
          )
        }

        if (isSelected) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Selected",
            tint = NothingWhite,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // Wallpaper Dimmer Slider
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(NothingSurfaceElevated)
          .border(1.dp, NothingBorder, RoundedCornerShape(14.dp))
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "WALLPAPER DIMMER // SCRIM",
            color = NothingWhite,
            fontSize = 11.5.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "${(settings.wallpaperDim * 100).toInt()}%",
            color = NothingTextSecondary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }
        Slider(
          value = settings.wallpaperDim,
          onValueChange = { onUpdateSettings(settings.copy(wallpaperDim = it)) },
          valueRange = 0f..0.8f,
          colors = SliderDefaults.colors(
            thumbColor = NothingWhite,
            activeTrackColor = NothingWhite,
            inactiveTrackColor = NothingBorder
          )
        )
      }
    }
  }
}

/**
 * 4. WIDGETS TAB
 */
@Composable
private fun WidgetsCustomizationTab(
  activeWidgets: List<LauncherWidget>,
  onAddWidget: (LauncherWidget) -> Unit,
  onRemoveWidget: (String) -> Unit,
  onResetWidgets: () -> Unit
) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(10.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "WIDGET SUITE (${WidgetType.values().size})",
          color = NothingTextSecondary,
          fontSize = 10.5.sp,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.sp
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onResetWidgets)
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Icon(
            imageVector = Icons.Default.RestartAlt,
            contentDescription = "Reset",
            tint = NothingRed,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "RESET ALL",
            color = NothingRed,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    items(WidgetType.values()) { type ->
      val isActive = activeWidgets.any { it.type == type }
      val shape = RoundedCornerShape(14.dp)

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .clip(shape)
          .background(NothingSurfaceElevated)
          .border(1.dp, NothingBorder, shape)
          .padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = type.title,
            color = NothingWhite,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = type.description,
            color = NothingTextSecondary,
            fontSize = 9.5.sp,
            fontFamily = FontFamily.Monospace
          )
        }

        if (isActive) {
          val activeWidget = activeWidgets.first { it.type == type }
          IconButton(
            onClick = { onRemoveWidget(activeWidget.id) },
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(NothingSurface)
              .border(1.dp, NothingBorder, CircleShape)
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Remove",
              tint = NothingRed,
              modifier = Modifier.size(16.dp)
            )
          }
        } else {
          IconButton(
            onClick = {
              onAddWidget(
                LauncherWidget(
                  id = "widget_${type.name.lowercase()}_${System.currentTimeMillis()}",
                  type = type,
                  pageIndex = 0,
                  colSpan = type.defaultWidthSpan,
                  rowSpan = type.defaultHeightSpan,
                  order = activeWidgets.size
                )
              )
            },
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(NothingWhite)
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Add Widget",
              tint = NothingBlack,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

/**
 * 5. NICHE & GLYPH CUSTOMIZATION TAB
 */
@Composable
private fun NicheAndGlyphTab(
  settings: LauncherSettings,
  onUpdateSettings: (LauncherSettings) -> Unit
) {
  var isLit by remember { mutableStateOf(false) }
  var mode by remember { mutableStateOf("TORCH") }

  val infiniteTransition = rememberInfiniteTransition(label = "glyphBeat")
  val beatAlpha by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(400, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "beatAlpha"
  )

  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    // 1. Accent Mode
    item {
      Text(
        text = "NOTHING ACCENT COLORWAY",
        color = NothingTextSecondary,
        fontSize = 10.5.sp,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        AccentMode.values().forEach { accent ->
          val isSelected = settings.accentMode == accent
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) NothingSurfaceElevated else NothingDarkBackground)
              .border(1.dp, if (isSelected) Color(accent.hexColor) else NothingBorder, RoundedCornerShape(12.dp))
              .clickable { onUpdateSettings(settings.copy(accentMode = accent)) }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(Color(accent.hexColor))
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = accent.label.split(" ")[0].uppercase(),
                color = if (isSelected) NothingWhite else NothingTextSecondary,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      }
    }

    // 2. Simulated Glyph Battery / Audio Light Bar
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(NothingSurfaceElevated)
          .border(1.dp, NothingBorder, RoundedCornerShape(14.dp))
          .padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
          Text(
            text = "TOP GLYPH METER RIBBON",
            color = NothingWhite,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "Real-time LED battery & system pulse strip across top status",
            color = NothingTextSecondary,
            fontSize = 9.5.sp,
            fontFamily = FontFamily.Monospace
          )
        }
        Switch(
          checked = settings.glyphMeterEnabled,
          onCheckedChange = { onUpdateSettings(settings.copy(glyphMeterEnabled = it)) },
          colors = SwitchDefaults.colors(
            checkedThumbColor = NothingWhite,
            checkedTrackColor = Color(settings.accentMode.hexColor),
            uncheckedThumbColor = NothingTextSecondary,
            uncheckedTrackColor = NothingDarkBackground,
            uncheckedBorderColor = NothingBorder
          )
        )
      }
    }

    // 3. Custom Ticker Text
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(NothingSurfaceElevated)
          .border(1.dp, NothingBorder, RoundedCornerShape(14.dp))
          .padding(14.dp)
      ) {
        Text(
          text = "CUSTOM HEADER MARQUEE",
          color = NothingWhite,
          fontSize = 11.5.sp,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Medium
        )
        Text(
          text = "Personalize your Nothing OS home screen banner:",
          color = NothingTextSecondary,
          fontSize = 9.5.sp,
          fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(NothingDarkBackground)
            .border(1.dp, NothingBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          BasicTextField(
            value = settings.customMarqueeText,
            onValueChange = { onUpdateSettings(settings.copy(customMarqueeText = it)) },
            singleLine = true,
            textStyle = TextStyle(
              color = NothingWhite,
              fontSize = 12.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            ),
            cursorBrush = SolidColor(NothingRed),
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    // 4. Interactive Phone Back Glyph LED Simulator
    item {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(NothingDarkBackground)
          .border(1.dp, NothingBorder, RoundedCornerShape(16.dp))
          .padding(14.dp)
      ) {
        Text(
          text = "REAR GLYPH INTERFACE MATRIX",
          color = NothingTextSecondary,
          fontSize = 10.5.sp,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Box(
          modifier = Modifier
            .size(width = 130.dp, height = 160.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(NothingBlack)
            .border(1.dp, NothingBorder, RoundedCornerShape(22.dp)),
          contentAlignment = Alignment.Center
        ) {
          Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            val lightColor = if (!isLit) Color(0x22FFFFFF) else {
              when (mode) {
                "BEAT" -> Color.White.copy(alpha = beatAlpha)
                else -> Color.White
              }
            }

            // Camera ring
            drawCircle(
              color = lightColor,
              radius = 18.dp.toPx(),
              center = Offset(size.width * 0.35f, size.height * 0.25f),
              style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // C-Strip
            drawArc(
              color = lightColor,
              startAngle = 45f,
              sweepAngle = 270f,
              useCenter = false,
              topLeft = Offset(size.width * 0.2f, size.height * 0.38f),
              size = androidx.compose.ui.geometry.Size(size.width * 0.6f, size.height * 0.45f),
              style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Bottom line
            drawLine(
              color = lightColor,
              start = Offset(size.width * 0.5f, size.height * 0.88f),
              end = Offset(size.width * 0.5f, size.height * 0.98f),
              strokeWidth = 2.5.dp.toPx(),
              cap = StrokeCap.Round
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          CustomTabPill("TORCH", isSelected = mode == "TORCH") { mode = "TORCH" }
          CustomTabPill("BEAT SYNC", isSelected = mode == "BEAT") { mode = "BEAT" }
          CustomTabPill("CHARGING", isSelected = mode == "CHARGING") { mode = "CHARGING" }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isLit) NothingRed else NothingWhite)
            .clickable { isLit = !isLit }
            .padding(horizontal = 18.dp, vertical = 8.dp)
        ) {
          Text(
            text = if (isLit) "DEACTIVATE GLYPHS" else "TEST GLYPH LEDS",
            color = NothingBlack,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
