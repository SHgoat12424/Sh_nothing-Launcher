package com.example.ui.drawer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppCategory
import com.example.model.AppInfo
import com.example.model.DrawerLayoutMode
import com.example.model.IconTheme
import com.example.ui.components.MonochromeAppIcon
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingDarkBackground
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingSurface
import com.example.ui.theme.NothingSurfaceElevated
import com.example.ui.theme.NothingTextPrimary
import com.example.ui.theme.NothingTextSecondary
import com.example.ui.theme.NothingWhite
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun AppDrawerScreen(
  apps: List<AppInfo>,
  iconTheme: IconTheme,
  drawerMode: DrawerLayoutMode,
  hapticEnabled: Boolean,
  onAppLaunch: (AppInfo) -> Unit,
  onTogglePin: (AppInfo) -> Unit,
  onToggleFavorite: (AppInfo) -> Unit,
  onToggleHide: (AppInfo) -> Unit,
  onChangeDrawerMode: (DrawerLayoutMode) -> Unit,
  onCloseDrawer: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onCloseDrawer)

  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

  fun triggerHaptic() {
    if (hapticEnabled) {
      try {
        vibrator?.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
      } catch (e: Exception) {
        // Ignored
      }
    }
  }

  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf(AppCategory.ALL) }
  var showHiddenAppsVault by remember { mutableStateOf(false) }
  var selectedAppForMenu by remember { mutableStateOf<AppInfo?>(null) }

  // Filter apps
  val filteredApps = remember(apps, searchQuery, selectedCategory, showHiddenAppsVault) {
    apps.filter { app ->
      val matchesHidden = if (showHiddenAppsVault) app.isHidden else !app.isHidden
      val matchesCategory = (selectedCategory == AppCategory.ALL) || (app.category == selectedCategory)
      val matchesSearch = searchQuery.isBlank() || app.label.contains(searchQuery, ignoreCase = true) || app.packageName.contains(searchQuery, ignoreCase = true)
      matchesHidden && matchesCategory && matchesSearch
    }
  }

  val gridState = rememberLazyGridState()
  val listState = rememberLazyListState()

  // Alphabet letters present in the filtered app list
  val alphabetLetters = remember(filteredApps) {
    filteredApps.map { it.label.firstOrNull()?.uppercaseChar() ?: '#' }
      .distinct()
      .sorted()
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(NothingDarkBackground)
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Top Search Header
      DrawerSearchBar(
        query = searchQuery,
        onQueryChange = { searchQuery = it },
        onClearQuery = { searchQuery = "" },
        drawerMode = drawerMode,
        onChangeDrawerMode = onChangeDrawerMode,
        showHiddenVault = showHiddenAppsVault,
        onToggleHiddenVault = {
          triggerHaptic()
          showHiddenAppsVault = !showHiddenAppsVault
        },
        onCloseDrawer = onCloseDrawer
      )

      // 2. Category Filter Pills
      DrawerCategoryTabs(
        selectedCategory = selectedCategory,
        onCategorySelect = {
          triggerHaptic()
          selectedCategory = it
        }
      )

      Spacer(modifier = Modifier.height(6.dp))

      // 3. Main App List / Grid with Alphabet Scrubber
      Box(modifier = Modifier.weight(1f)) {
        if (filteredApps.isEmpty()) {
          EmptyDrawerState(showHidden = showHiddenAppsVault)
        } else {
          when (drawerMode) {
            DrawerLayoutMode.GRID_4, DrawerLayoutMode.GRID_3 -> {
              val columns = drawerMode.columns
              LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                state = gridState,
                contentPadding = PaddingValues(start = 16.dp, end = 36.dp, top = 8.dp, bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().testTag("app_grid")
              ) {
                items(filteredApps, key = { it.id }) { app ->
                  AppGridItem(
                    app = app,
                    iconTheme = iconTheme,
                    onClick = {
                      triggerHaptic()
                      onAppLaunch(app)
                    },
                    onLongClick = {
                      triggerHaptic()
                      selectedAppForMenu = app
                    }
                  )
                }
              }
            }
            DrawerLayoutMode.COMPACT_LIST -> {
              LazyColumn(
                state = listState,
                contentPadding = PaddingValues(start = 16.dp, end = 36.dp, top = 8.dp, bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize().testTag("app_list")
              ) {
                items(filteredApps, key = { it.id }) { app ->
                  AppListItem(
                    app = app,
                    iconTheme = iconTheme,
                    onClick = {
                      triggerHaptic()
                      onAppLaunch(app)
                    },
                    onLongClick = {
                      triggerHaptic()
                      selectedAppForMenu = app
                    }
                  )
                }
              }
            }
          }
        }

        // 4. Alphabetical Quick Scrubber Bar on right edge
        if (alphabetLetters.size > 2) {
          AlphabetScrubber(
            letters = alphabetLetters,
            onLetterSelected = { letter ->
              triggerHaptic()
              val targetIndex = filteredApps.indexOfFirst {
                it.label.firstOrNull()?.uppercaseChar() == letter
              }
              if (targetIndex >= 0) {
                coroutineScope.launch {
                  if (drawerMode == DrawerLayoutMode.COMPACT_LIST) {
                    listState.scrollToItem(targetIndex)
                  } else {
                    gridState.scrollToItem(targetIndex)
                  }
                }
              }
            },
            modifier = Modifier
              .align(Alignment.CenterEnd)
              .padding(end = 4.dp)
          )
        }
      }
    }

    // App Long Press Context Sheet
    selectedAppForMenu?.let { app ->
      AppActionBottomSheet(
        app = app,
        onDismiss = { selectedAppForMenu = null },
        onTogglePin = {
          onTogglePin(app)
          selectedAppForMenu = null
        },
        onToggleFavorite = {
          onToggleFavorite(app)
          selectedAppForMenu = null
        },
        onToggleHide = {
          onToggleHide(app)
          selectedAppForMenu = null
        },
        onAppInfo = {
          try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
              data = Uri.fromParts("package", app.packageName, null)
              flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
          } catch (e: Exception) {
            // Ignored
          }
          selectedAppForMenu = null
        },
        onUninstall = {
          try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
              data = Uri.fromParts("package", app.packageName, null)
              flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
          } catch (e: Exception) {
            // Ignored
          }
          selectedAppForMenu = null
        }
      )
    }
  }
}

@Composable
private fun DrawerSearchBar(
  query: String,
  onQueryChange: (String) -> Unit,
  onClearQuery: () -> Unit,
  drawerMode: DrawerLayoutMode,
  onChangeDrawerMode: (DrawerLayoutMode) -> Unit,
  showHiddenVault: Boolean,
  onToggleHiddenVault: () -> Unit,
  onCloseDrawer: () -> Unit
) {
  val shape = RoundedCornerShape(22.dp)

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Search Input Field
    Box(
      modifier = Modifier
        .weight(1f)
        .height(46.dp)
        .clip(shape)
        .background(NothingSurface)
        .border(1.dp, if (showHiddenVault) NothingRed else NothingBorder, shape)
        .padding(horizontal = 14.dp),
      contentAlignment = Alignment.CenterStart
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search",
          tint = if (showHiddenVault) NothingRed else NothingTextSecondary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Box(modifier = Modifier.weight(1f)) {
          if (query.isEmpty()) {
            Text(
              text = if (showHiddenVault) "HIDDEN VAULT" else "SEARCH APPS...",
              color = NothingTextSecondary,
              fontSize = 13.sp,
              fontFamily = FontFamily.Monospace,
              letterSpacing = 1.sp
            )
          }
          BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(
              color = NothingWhite,
              fontSize = 14.sp,
              fontFamily = FontFamily.Monospace
            ),
            cursorBrush = SolidColor(NothingRed),
            modifier = Modifier.fillMaxWidth().testTag("drawer_search_input")
          )
        }

        if (query.isNotEmpty()) {
          IconButton(
            onClick = onClearQuery,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Clear",
              tint = NothingTextSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.width(8.dp))

    // Hidden Vault Toggle Button
    IconButton(
      onClick = onToggleHiddenVault,
      modifier = Modifier
        .size(42.dp)
        .clip(CircleShape)
        .background(if (showHiddenVault) NothingRed else NothingSurface)
        .border(1.dp, if (showHiddenVault) NothingRed else NothingBorder, CircleShape)
        .testTag("vault_btn")
    ) {
      Icon(
        imageVector = if (showHiddenVault) Icons.Default.LockOpen else Icons.Default.Lock,
        contentDescription = "Vault",
        tint = if (showHiddenVault) NothingWhite else NothingTextSecondary,
        modifier = Modifier.size(18.dp)
      )
    }

    Spacer(modifier = Modifier.width(6.dp))

    // Layout Mode Toggle Button
    IconButton(
      onClick = {
        val next = when (drawerMode) {
          DrawerLayoutMode.GRID_4 -> DrawerLayoutMode.GRID_3
          DrawerLayoutMode.GRID_3 -> DrawerLayoutMode.COMPACT_LIST
          DrawerLayoutMode.COMPACT_LIST -> DrawerLayoutMode.GRID_4
        }
        onChangeDrawerMode(next)
      },
      modifier = Modifier
        .size(42.dp)
        .clip(CircleShape)
        .background(NothingSurface)
        .border(1.dp, NothingBorder, CircleShape)
        .testTag("layout_mode_btn")
    ) {
      Icon(
        imageVector = when (drawerMode) {
          DrawerLayoutMode.GRID_4 -> Icons.Default.GridView
          DrawerLayoutMode.GRID_3 -> Icons.Default.Grid3x3
          DrawerLayoutMode.COMPACT_LIST -> Icons.Default.ViewList
        },
        contentDescription = "Switch Layout",
        tint = NothingTextSecondary,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

@Composable
private fun DrawerCategoryTabs(
  selectedCategory: AppCategory,
  onCategorySelect: (AppCategory) -> Unit
) {
  ScrollableTabRow(
    selectedTabIndex = selectedCategory.ordinal,
    edgePadding = 16.dp,
    containerColor = NothingDarkBackground,
    contentColor = NothingWhite,
    divider = {},
    indicator = {}
  ) {
    AppCategory.values().forEach { category ->
      val isSelected = selectedCategory == category
      val shape = RoundedCornerShape(14.dp)
      Box(
        modifier = Modifier
          .padding(end = 8.dp)
          .clip(shape)
          .background(if (isSelected) NothingWhite else NothingSurface)
          .border(1.dp, if (isSelected) NothingWhite else NothingBorder, shape)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(),
            onClick = { onCategorySelect(category) }
          )
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Text(
          text = category.displayName.uppercase(),
          color = if (isSelected) NothingBlack else NothingTextSecondary,
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
          letterSpacing = 1.sp
        )
      }
    }
  }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun AppGridItem(
  app: AppInfo,
  iconTheme: IconTheme,
  onClick: () -> Unit,
  onLongClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .combinedClickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(),
        onClick = onClick,
        onLongClick = onLongClick
      )
      .padding(vertical = 4.dp)
  ) {
    MonochromeAppIcon(
      app = app,
      iconTheme = iconTheme,
      size = 54.dp
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = app.label,
      color = NothingTextPrimary,
      fontSize = 11.sp,
      fontFamily = FontFamily.Monospace,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      textAlign = TextAlign.Center
    )
  }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun AppListItem(
  app: AppInfo,
  iconTheme: IconTheme,
  onClick: () -> Unit,
  onLongClick: () -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(NothingSurface)
      .border(1.dp, NothingBorder, RoundedCornerShape(16.dp))
      .combinedClickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(),
        onClick = onClick,
        onLongClick = onLongClick
      )
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    MonochromeAppIcon(
      app = app,
      iconTheme = iconTheme,
      size = 42.dp
    )
    Spacer(modifier = Modifier.width(14.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = app.label,
        color = NothingTextPrimary,
        fontSize = 13.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium
      )
      Text(
        text = app.category.displayName.uppercase(),
        color = NothingTextSecondary,
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 0.5.sp
      )
    }

    if (app.isPinnedHome) {
      Icon(
        imageVector = Icons.Default.PushPin,
        contentDescription = "Pinned",
        tint = NothingRed,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}

@Composable
private fun AlphabetScrubber(
  letters: List<Char>,
  onLetterSelected: (Char) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .width(24.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(NothingSurface)
      .border(1.dp, NothingBorder, RoundedCornerShape(12.dp))
      .padding(vertical = 4.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceEvenly
  ) {
    letters.forEach { char ->
      Box(
        modifier = Modifier
          .size(18.dp)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { onLetterSelected(char) }
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = char.toString(),
          color = NothingTextSecondary,
          fontSize = 9.sp,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
private fun EmptyDrawerState(showHidden: Boolean) {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(CircleShape)
          .background(NothingSurface)
          .border(1.dp, NothingBorder, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (showHidden) Icons.Default.Lock else Icons.Default.Search,
          contentDescription = null,
          tint = NothingTextSecondary,
          modifier = Modifier.size(24.dp)
        )
      }
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = if (showHidden) "VAULT IS EMPTY" else "NO APPS FOUND",
        color = NothingTextPrimary,
        fontSize = 13.sp,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = if (showHidden) "Long-press any app in drawer to hide it here" else "Try a different search query or category",
        color = NothingTextSecondary,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppActionBottomSheet(
  app: AppInfo,
  onDismiss: () -> Unit,
  onTogglePin: () -> Unit,
  onToggleFavorite: () -> Unit,
  onToggleHide: () -> Unit,
  onAppInfo: () -> Unit,
  onUninstall: () -> Unit
) {
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(),
    containerColor = NothingSurface,
    contentColor = NothingWhite,
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(top = 12.dp, bottom = 8.dp)
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
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .navigationBarsPadding()
    ) {
      // Header
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
      ) {
        MonochromeAppIcon(app = app, size = 42.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = app.label,
            color = NothingWhite,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = app.packageName,
            color = NothingTextSecondary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      HorizontalDivider(color = NothingBorder, thickness = 1.dp)
      Spacer(modifier = Modifier.height(10.dp))

      // Action 1: Pin to Home
      ActionRow(
        icon = Icons.Default.PushPin,
        label = if (app.isPinnedHome) "UNPIN FROM DOCK / HOME" else "PIN TO DOCK / HOME",
        iconColor = if (app.isPinnedHome) NothingRed else NothingWhite,
        onClick = onTogglePin
      )

      // Action 2: Favorite
      ActionRow(
        icon = Icons.Default.Star,
        label = if (app.isFavorite) "REMOVE FROM FAVORITES" else "ADD TO FAVORITES",
        iconColor = if (app.isFavorite) NothingRed else NothingWhite,
        onClick = onToggleFavorite
      )

      // Action 3: Hide App
      ActionRow(
        icon = Icons.Default.VisibilityOff,
        label = if (app.isHidden) "UNHIDE APP" else "HIDE IN PRIVATE VAULT",
        iconColor = NothingWhite,
        onClick = onToggleHide
      )

      // Action 4: System App Info
      ActionRow(
        icon = Icons.Default.Info,
        label = "APP INFO & PERMISSIONS",
        iconColor = NothingWhite,
        onClick = onAppInfo
      )

      // Action 5: Uninstall
      ActionRow(
        icon = Icons.Default.Delete,
        label = "UNINSTALL APPLICATION",
        iconColor = NothingRed,
        onClick = onUninstall
      )

      Spacer(modifier = Modifier.height(14.dp))
    }
  }
}

@Composable
private fun ActionRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  iconColor: androidx.compose.ui.graphics.Color,
  onClick: () -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(),
        onClick = onClick
      )
      .padding(horizontal = 10.dp, vertical = 12.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = iconColor,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.width(14.dp))
    Text(
      text = label,
      color = NothingTextPrimary,
      fontSize = 12.sp,
      fontFamily = FontFamily.Monospace,
      fontWeight = FontWeight.Medium,
      letterSpacing = 0.5.sp
    )
  }
}
