package com.example.ui.home

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.model.DockStyle
import com.example.model.LauncherSettings
import com.example.model.LauncherWidget
import com.example.ui.components.DotMatrixText
import com.example.ui.components.MonochromeAppIcon
import com.example.ui.components.NothingWidgetContainer
import com.example.ui.components.WallpaperBackground
import com.example.ui.customization.CustomizationBottomSheet
import com.example.ui.drawer.AppDrawerScreen
import com.example.ui.quickglance.QuickGlanceBottomSheet
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
  viewModel: LauncherViewModel,
  modifier: Modifier = Modifier
) {
  val apps by viewModel.apps.collectAsState()
  val widgets by viewModel.widgets.collectAsState()
  val settings by viewModel.settings.collectAsState()

  val isDrawerOpen by viewModel.isDrawerOpen.collectAsState()
  val isQuickGlanceOpen by viewModel.isQuickGlanceOpen.collectAsState()
  val isCustomizationOpen by viewModel.isCustomizationOpen.collectAsState()
  val isSleeping by viewModel.isSleeping.collectAsState()

  val context = LocalContext.current
  val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }
  val coroutineScope = rememberCoroutineScope()

  fun triggerHaptic(durationMs: Long = 12) {
    if (settings.hapticFeedback) {
      try {
        vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
      } catch (e: Exception) {
        // Ignored
      }
    }
  }

  var dragOffsetY by remember { mutableFloatStateOf(0f) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .pointerInput(settings.doubleTapToSleep, isDrawerOpen) {
        if (!isDrawerOpen) {
          detectTapGestures(
            onDoubleTap = {
              if (settings.doubleTapToSleep) {
                triggerHaptic(20)
                viewModel.sleepScreen()
              }
            },
            onLongPress = {
              triggerHaptic(25)
              viewModel.openCustomization()
            }
          )
        }
      }
      .pointerInput(isDrawerOpen) {
        if (!isDrawerOpen) {
          detectDragGestures(
            onDrag = { change, dragAmount ->
              change.consume()
              dragOffsetY += dragAmount.y
            },
            onDragEnd = {
              if (dragOffsetY < -100f) {
                // Swipe Up -> Open App Drawer
                triggerHaptic()
                viewModel.openDrawer()
              } else if (dragOffsetY > 100f && settings.swipeDownForGlance) {
                // Swipe Down -> Open Quick Glance
                triggerHaptic()
                viewModel.openQuickGlance()
              }
              dragOffsetY = 0f
            },
            onDragCancel = {
              dragOffsetY = 0f
            }
          )
        }
      }
  ) {
    // 1. Wallpaper Background with Custom Dimming
    WallpaperBackground(
      wallpaperStyle = settings.wallpaperStyle,
      wallpaperDim = settings.wallpaperDim
    )

    // 2. Home Screen Pages (Pager for widgets)
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
    ) {
      // Niche: Simulated Top Glyph Light Meter Ribbon
      if (settings.glyphMeterEnabled) {
        TopGlyphLightMeter(accentColor = Color(settings.accentMode.hexColor))
      }

      // Compact Top Header with Custom Marquee
      HomeTopHeader(
        pageIndex = pagerState.currentPage,
        pageCount = pagerState.pageCount,
        customMarquee = settings.customMarqueeText,
        accentColor = Color(settings.accentMode.hexColor),
        onOpenCustomization = {
          triggerHaptic()
          viewModel.openCustomization()
        },
        onOpenGlance = {
          triggerHaptic()
          viewModel.openQuickGlance()
        }
      )

      // Paged Widget Workspace (Compact dimensions)
      HorizontalPager(
        state = pagerState,
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
      ) { pageIndex ->
        val pageWidgets = widgets.filter { it.pageIndex == pageIndex }.ifEmpty {
          if (pageIndex == 0) widgets.take(4) else widgets.drop(4)
        }

        HomeWidgetsGrid(
          widgets = pageWidgets,
          installedApps = apps,
          onAppLaunch = { app ->
            triggerHaptic()
            viewModel.launchApp(app)
          },
          onWidgetLongClick = {
            triggerHaptic(20)
            viewModel.openCustomization()
          }
        )
      }

      // Custom Bottom Dock & Drawer Handle
      if (settings.dockStyle != DockStyle.HIDDEN) {
        HomeBottomDock(
          pinnedApps = apps.filter { it.isPinnedHome }.take(settings.dockAppCount),
          settings = settings,
          onAppLaunch = { app ->
            triggerHaptic()
            viewModel.launchApp(app)
          },
          onSwipeUpClick = {
            triggerHaptic()
            viewModel.openDrawer()
          }
        )
      } else {
        // If dock hidden, render minimalist drawer swipe handle
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .clickable(onClick = { viewModel.openDrawer() })
              .padding(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.KeyboardArrowUp,
              contentDescription = "Drawer",
              tint = NothingTextSecondary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "SWIPE UP",
              color = NothingTextSecondary,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace,
              letterSpacing = 1.sp
            )
          }
        }
      }
    }

    // 3. Ultra-fluid Animated App Drawer
    AnimatedVisibility(
      visible = isDrawerOpen,
      enter = slideInVertically(
        initialOffsetY = { it },
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioLowBouncy,
          stiffness = Spring.StiffnessMediumLow
        )
      ) + fadeIn(animationSpec = tween(200)),
      exit = slideOutVertically(
        targetOffsetY = { it },
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioNoBouncy,
          stiffness = Spring.StiffnessMedium
        )
      ) + fadeOut(animationSpec = tween(180))
    ) {
      AppDrawerScreen(
        apps = apps,
        iconTheme = settings.iconTheme,
        drawerMode = settings.drawerLayoutMode,
        hapticEnabled = settings.hapticFeedback,
        onAppLaunch = { app ->
          viewModel.launchApp(app)
        },
        onTogglePin = { app ->
          triggerHaptic()
          viewModel.togglePin(app)
        },
        onToggleFavorite = { app ->
          triggerHaptic()
          viewModel.toggleFavorite(app)
        },
        onToggleHide = { app ->
          triggerHaptic()
          viewModel.toggleHide(app)
        },
        onChangeDrawerMode = { mode ->
          triggerHaptic()
          viewModel.updateSettings(settings.copy(drawerLayoutMode = mode))
        },
        onCloseDrawer = {
          viewModel.closeDrawer()
        }
      )
    }

    // 4. Quick Glance Sheet
    if (isQuickGlanceOpen) {
      QuickGlanceBottomSheet(
        onDismiss = { viewModel.closeQuickGlance() },
        onOpenSettings = {
          viewModel.closeQuickGlance()
          viewModel.openCustomization()
        }
      )
    }

    // 5. Customization Hub Sheet
    if (isCustomizationOpen) {
      CustomizationBottomSheet(
        currentSettings = settings,
        activeWidgets = widgets,
        installedApps = apps,
        onUpdateSettings = { newSettings ->
          triggerHaptic()
          viewModel.updateSettings(newSettings)
        },
        onAddWidget = { widget ->
          triggerHaptic()
          viewModel.addWidget(widget)
        },
        onRemoveWidget = { id ->
          triggerHaptic()
          viewModel.removeWidget(id)
        },
        onResetWidgets = {
          triggerHaptic(25)
          viewModel.resetWidgets()
        },
        onUpdateCustomGlyph = { appId, glyph ->
          triggerHaptic()
          viewModel.setCustomGlyph(appId, glyph)
        },
        onDismiss = { viewModel.closeCustomization() }
      )
    }

    // 6. Ambient Screen Sleep / Lock Effect
    AnimatedVisibility(
      visible = isSleeping,
      enter = fadeIn(animationSpec = tween(250)),
      exit = fadeOut(animationSpec = tween(200))
    ) {
      AmbientSleepOverlay(onWake = {
        triggerHaptic()
        viewModel.wakeScreen()
      })
    }
  }
}

/**
 * Niche: Top Glyph Light Meter Ribbon
 */
@Composable
private fun TopGlyphLightMeter(accentColor: Color) {
  val infiniteTransition = rememberInfiniteTransition(label = "glyphMeterPulse")
  val pulse by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  Canvas(
    modifier = Modifier
      .fillMaxWidth()
      .height(4.dp)
      .padding(horizontal = 24.dp)
  ) {
    val segWidth = size.width / 16f
    for (i in 0 until 16) {
      val isCenter = i in 6..9
      val color = if (isCenter) accentColor.copy(alpha = pulse) else Color(0x33FFFFFF)
      drawLine(
        color = color,
        start = Offset(i * segWidth + 2.dp.toPx(), size.height / 2f),
        end = Offset((i + 1) * segWidth - 2.dp.toPx(), size.height / 2f),
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round
      )
    }
  }
}

@Composable
private fun HomeTopHeader(
  pageIndex: Int,
  pageCount: Int,
  customMarquee: String,
  accentColor: Color,
  onOpenCustomization: () -> Unit,
  onOpenGlance: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 18.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Left: Nothing OS branding / custom ticker
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(10.dp))
        .clickable(onClick = onOpenGlance)
        .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
      Box(
        modifier = Modifier
          .size(5.dp)
          .clip(CircleShape)
          .background(accentColor)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = customMarquee.ifBlank { "NOTHING OS (2.5)" },
        color = NothingTextPrimary,
        fontSize = 10.5.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    // Center: Dot matrix page indicator
    Row(
      horizontalArrangement = Arrangement.spacedBy(5.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      repeat(pageCount) { index ->
        val isCurrent = index == pageIndex
        Box(
          modifier = Modifier
            .size(if (isCurrent) 5.dp else 3.5.dp)
            .clip(CircleShape)
            .background(if (isCurrent) NothingWhite else Color(0x33FFFFFF))
        )
      }
    }

    // Right: Customization studio button
    IconButton(
      onClick = onOpenCustomization,
      modifier = Modifier
        .size(30.dp)
        .clip(CircleShape)
        .background(NothingSurface)
        .border(1.dp, NothingBorder, CircleShape)
        .testTag("customization_btn")
    ) {
      Icon(
        imageVector = Icons.Default.Tune,
        contentDescription = "Customization",
        tint = NothingWhite,
        modifier = Modifier.size(15.dp)
      )
    }
  }
}

/**
 * Compact Widgets Grid
 */
@Composable
private fun HomeWidgetsGrid(
  widgets: List<LauncherWidget>,
  installedApps: List<AppInfo>,
  onAppLaunch: (AppInfo) -> Unit,
  onWidgetLongClick: () -> Unit
) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    items(widgets, key = { it.id }, span = { widget ->
      val span = if (widget.colSpan >= 4) 2 else 1
      GridItemSpan(span)
    }) { widget ->
      val height = when (widget.rowSpan) {
        1 -> 76.dp // Ultra compact
        2 -> 146.dp // Compact 2-row
        else -> 140.dp
      }

      NothingWidgetContainer(
        widget = widget,
        onLongClick = onWidgetLongClick,
        onAppLaunch = onAppLaunch,
        installedApps = installedApps,
        modifier = Modifier
          .fillMaxWidth()
          .height(height)
          .testTag("widget_${widget.type.name.lowercase()}")
      )
    }
  }
}

/**
 * Customizable Bottom Dock
 */
@Composable
private fun HomeBottomDock(
  pinnedApps: List<AppInfo>,
  settings: LauncherSettings,
  onAppLaunch: (AppInfo) -> Unit,
  onSwipeUpClick: () -> Unit
) {
  val appsToDisplay = remember(pinnedApps, settings.dockAppCount) {
    val fallback = listOf(
      AppInfo("dock_phone", "com.nothing.phone", "", "Phone", iconGlyph = "phone"),
      AppInfo("dock_chat", "com.nothing.messages", "", "Chat", iconGlyph = "chat"),
      AppInfo("dock_camera", "com.nothing.camera", "", "Camera", iconGlyph = "camera"),
      AppInfo("dock_browser", "com.nothing.browser", "", "Web", iconGlyph = "browser"),
      AppInfo("dock_music", "com.nothing.music", "", "Music", iconGlyph = "music")
    )
    val list = (pinnedApps + fallback.filter { fb -> pinnedApps.none { it.packageName == fb.packageName } })
    list.take(settings.dockAppCount)
  }

  val iconSize = (46.dp * settings.iconScale)

  val dockModifier = when (settings.dockStyle) {
    DockStyle.GLASS_PILL -> Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .background(NothingSurface.copy(alpha = 0.85f))
      .border(1.dp, NothingBorder, RoundedCornerShape(24.dp))
      .padding(horizontal = 10.dp, vertical = 6.dp)

    DockStyle.FLOATING_MINIMAL -> Modifier
      .fillMaxWidth()
      .padding(horizontal = 10.dp, vertical = 4.dp)

    DockStyle.DOTTED_BORDER -> Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .background(NothingDarkBackground.copy(alpha = 0.9f))
      .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(24.dp))
      .padding(horizontal = 10.dp, vertical = 6.dp)

    DockStyle.HIDDEN -> Modifier
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 2.dp)
  ) {
    // Dock Icons Container
    Row(
      modifier = dockModifier,
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      appsToDisplay.forEach { app ->
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = ripple(),
              onClick = { onAppLaunch(app) }
            )
            .padding(2.dp)
            .testTag("dock_${app.iconGlyph}")
        ) {
          MonochromeAppIcon(
            app = app,
            iconTheme = settings.iconTheme,
            iconShape = settings.iconShape,
            accentColor = Color(settings.accentMode.hexColor),
            size = iconSize
          )
          if (settings.showAppLabels) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = app.label,
              color = NothingTextPrimary,
              fontSize = 8.5.sp,
              fontFamily = FontFamily.Monospace,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Drawer Swipe Handle Pill
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(10.dp))
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = ripple(),
          onClick = onSwipeUpClick
        )
        .padding(horizontal = 14.dp, vertical = 3.dp)
        .testTag("drawer_handle")
    ) {
      Icon(
        imageVector = Icons.Default.KeyboardArrowUp,
        contentDescription = "Open Drawer",
        tint = NothingTextSecondary,
        modifier = Modifier.size(15.dp)
      )
      Spacer(modifier = Modifier.width(3.dp))
      Text(
        text = "SWIPE UP",
        color = NothingTextSecondary,
        fontSize = 8.5.sp,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )
    }
  }
}

/**
 * Ambient Sleep Screen Display (Double Tap to Sleep)
 */
@Composable
private fun AmbientSleepOverlay(onWake: () -> Unit) {
  val currentTime = remember {
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
  }
  val currentDate = remember {
    SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()).uppercase()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(NothingBlack)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onWake
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      DotMatrixText(
        text = currentTime,
        dotRadius = 2.8.dp,
        dotSpacing = 1.5.dp,
        charSpacing = 5.dp,
        activeColor = LedActiveWhite
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = currentDate,
        color = NothingTextSecondary,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.5.sp
      )

      Spacer(modifier = Modifier.height(42.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(5.dp)
            .clip(CircleShape)
            .background(NothingRed)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "TOUCH ANYWHERE TO WAKE",
          color = Color(0x55FFFFFF),
          fontSize = 10.sp,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.sp
        )
      }
    }
  }
}
