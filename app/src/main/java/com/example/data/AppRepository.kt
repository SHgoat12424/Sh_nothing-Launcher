package com.example.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.example.data.db.AppMetadataEntity
import com.example.data.db.LauncherDao
import com.example.data.db.SettingsEntity
import com.example.data.db.WidgetEntity
import com.example.model.AppCategory
import com.example.model.AppInfo
import com.example.model.DrawerLayoutMode
import com.example.model.IconTheme
import com.example.model.LauncherSettings
import com.example.model.LauncherWidget
import com.example.model.WallpaperStyle
import com.example.model.WidgetType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AppRepository(
  private val context: Context,
  private val dao: LauncherDao
) {
  private val packageManager: PackageManager = context.packageManager

  // Flow of installed and curated apps merged with metadata
  fun getInstalledApps(): Flow<List<AppInfo>> {
    val rawAppsFlow = flow {
      val apps = loadSystemAndCuratedApps()
      emit(apps)
    }.flowOn(Dispatchers.IO)

    val metadataFlow = dao.getAllAppMetadata()

    return combine(rawAppsFlow, metadataFlow) { apps, metadataList ->
      val metaMap = metadataList.associateBy { it.id }
      apps.map { app ->
        val meta = metaMap[app.id]
        if (meta != null) {
          app.copy(
            isFavorite = meta.isFavorite,
            isHidden = meta.isHidden,
            isPinnedHome = meta.isPinnedHome,
            launchCount = meta.launchCount,
            iconGlyph = if (meta.customGlyph.isNotEmpty()) meta.customGlyph else app.iconGlyph,
            category = if (meta.customCategory.isNotEmpty()) {
              try {
                AppCategory.valueOf(meta.customCategory)
              } catch (e: Exception) {
                app.category
              }
            } else app.category
          )
        } else {
          app
        }
      }
    }
  }

  private fun loadSystemAndCuratedApps(): List<AppInfo> {
    val intent = Intent(Intent.ACTION_MAIN, null).apply {
      addCategory(Intent.CATEGORY_LAUNCHER)
    }

    val resolveInfos = try {
      packageManager.queryIntentActivities(intent, 0)
    } catch (e: Exception) {
      emptyList()
    }

    val installedList = mutableListOf<AppInfo>()
    val seenPackages = mutableSetOf<String>()

    for (resolveInfo in resolveInfos) {
      val pkg = resolveInfo.activityInfo.packageName
      if (pkg == context.packageName) continue // don't show the launcher itself inside its own drawer
      val activity = resolveInfo.activityInfo.name
      val label = resolveInfo.loadLabel(packageManager).toString()
      val glyph = inferGlyph(pkg, label)
      val category = inferCategory(pkg, label)

      installedList.add(
        AppInfo(
          id = "$pkg/$activity",
          packageName = pkg,
          activityName = activity,
          label = label,
          category = category,
          iconGlyph = glyph,
          isFavorite = isDefaultFavorite(pkg, label),
          isPinnedHome = isDefaultPinned(pkg, label)
        )
      )
      seenPackages.add(pkg)
    }

    // Curate Nothing OS essential built-ins if not present on emulator
    val curatedDefaults = listOf(
      AppInfo("com.nothing.camera", "com.nothing.camera", "com.nothing.camera.CameraActivity", "Camera", AppCategory.ESSENTIALS, isFavorite = true, isPinnedHome = true, iconGlyph = "camera"),
      AppInfo("com.nothing.phone", "com.nothing.phone", "com.nothing.phone.DialerActivity", "Phone", AppCategory.ESSENTIALS, isFavorite = true, isPinnedHome = true, iconGlyph = "phone"),
      AppInfo("com.nothing.messages", "com.nothing.messages", "com.nothing.messages.ChatActivity", "Messages", AppCategory.ESSENTIALS, isFavorite = true, isPinnedHome = true, iconGlyph = "chat"),
      AppInfo("com.nothing.browser", "com.nothing.browser", "com.nothing.browser.BrowserActivity", "Browser", AppCategory.TOOLS, isFavorite = true, isPinnedHome = true, iconGlyph = "browser"),
      AppInfo("com.nothing.gallery", "com.nothing.gallery", "com.nothing.gallery.GalleryActivity", "Gallery", AppCategory.MEDIA, isFavorite = true, isPinnedHome = false, iconGlyph = "gallery"),
      AppInfo("com.nothing.settings", "com.nothing.settings", "com.nothing.settings.SettingsActivity", "Settings", AppCategory.SYSTEM, isFavorite = false, isPinnedHome = false, iconGlyph = "settings"),
      AppInfo("com.nothing.music", "com.nothing.music", "com.nothing.music.PlayerActivity", "Music", AppCategory.MEDIA, isFavorite = true, isPinnedHome = false, iconGlyph = "music"),
      AppInfo("com.nothing.calculator", "com.nothing.calculator", "com.nothing.calculator.CalcActivity", "Calculator", AppCategory.TOOLS, isFavorite = false, isPinnedHome = false, iconGlyph = "calculator"),
      AppInfo("com.nothing.clock", "com.nothing.clock", "com.nothing.clock.ClockActivity", "Clock", AppCategory.TOOLS, isFavorite = false, isPinnedHome = false, iconGlyph = "clock"),
      AppInfo("com.nothing.notes", "com.nothing.notes", "com.nothing.notes.NotesActivity", "Notes", AppCategory.TOOLS, isFavorite = false, isPinnedHome = false, iconGlyph = "notes"),
      AppInfo("com.nothing.recorder", "com.nothing.recorder", "com.nothing.recorder.RecorderActivity", "Recorder", AppCategory.MEDIA, isFavorite = false, isPinnedHome = false, iconGlyph = "recorder"),
      AppInfo("com.nothing.weather", "com.nothing.weather", "com.nothing.weather.WeatherActivity", "Weather", AppCategory.TOOLS, isFavorite = false, isPinnedHome = false, iconGlyph = "weather"),
      AppInfo("com.nothing.files", "com.nothing.files", "com.nothing.files.FilesActivity", "Files", AppCategory.TOOLS, isFavorite = false, isPinnedHome = false, iconGlyph = "files"),
      AppInfo("com.nothing.maps", "com.nothing.maps", "com.nothing.maps.MapsActivity", "Maps", AppCategory.TOOLS, isFavorite = false, isPinnedHome = false, iconGlyph = "maps")
    )

    for (curated in curatedDefaults) {
      if (!seenPackages.contains(curated.packageName)) {
        installedList.add(curated)
      }
    }

    return installedList.sortedBy { it.label.lowercase() }
  }

  private fun inferGlyph(pkg: String, label: String): String {
    val low = (pkg + " " + label).lowercase()
    return when {
      "camera" in low -> "camera"
      "dialer" in low || "phone" in low || "call" in low -> "phone"
      "message" in low || "chat" in low || "sms" in low || "whatsapp" in low || "telegram" in low -> "chat"
      "browser" in low || "chrome" in low || "firefox" in low || "web" in low -> "browser"
      "setting" in low || "config" in low -> "settings"
      "gallery" in low || "photo" in low || "image" in low -> "gallery"
      "music" in low || "audio" in low || "spotify" in low || "sound" in low -> "music"
      "calc" in low -> "calculator"
      "clock" in low || "alarm" in low || "timer" in low -> "clock"
      "note" in low || "keep" in low || "memo" in low -> "notes"
      "record" in low || "voice" in low -> "recorder"
      "weather" in low -> "weather"
      "file" in low || "download" in low || "folder" in low -> "files"
      "map" in low || "navigation" in low || "gps" in low -> "maps"
      "mail" in low || "gmail" in low || "outlook" in low -> "mail"
      "video" in low || "youtube" in low || "movie" in low -> "video"
      else -> "app"
    }
  }

  private fun inferCategory(pkg: String, label: String): AppCategory {
    val low = (pkg + " " + label).lowercase()
    return when {
      "phone" in low || "dialer" in low || "camera" in low || "message" in low || "contact" in low -> AppCategory.ESSENTIALS
      "browser" in low || "calc" in low || "clock" in low || "note" in low || "file" in low || "tool" in low || "weather" in low || "map" in low -> AppCategory.TOOLS
      "chat" in low || "whatsapp" in low || "telegram" in low || "social" in low || "twitter" in low || "instagram" in low || "facebook" in low -> AppCategory.SOCIAL
      "music" in low || "media" in low || "photo" in low || "gallery" in low || "video" in low || "youtube" in low || "sound" in low -> AppCategory.MEDIA
      "game" in low || "play" in low -> AppCategory.GAMES
      "setting" in low || "system" in low || "android" in low || "store" in low -> AppCategory.SYSTEM
      else -> AppCategory.ESSENTIALS
    }
  }

  private fun isDefaultFavorite(pkg: String, label: String): Boolean {
    val low = (pkg + " " + label).lowercase()
    return "camera" in low || "phone" in low || "message" in low || "browser" in low || "gallery" in low
  }

  private fun isDefaultPinned(pkg: String, label: String): Boolean {
    val low = (pkg + " " + label).lowercase()
    return "phone" in low || "message" in low || "browser" in low || "camera" in low
  }

  // Widgets persistence
  fun getWidgets(): Flow<List<LauncherWidget>> {
    return dao.getAllWidgets().map { entities ->
      if (entities.isEmpty()) {
        getDefaultWidgets()
      } else {
        entities.map { it.toModel() }
      }
    }
  }

  private fun getDefaultWidgets(): List<LauncherWidget> {
    return listOf(
      LauncherWidget(
        id = "widget_digital_clock",
        type = WidgetType.DIGITAL_CLOCK,
        pageIndex = 0,
        colSpan = 2,
        rowSpan = 2,
        order = 0
      ),
      LauncherWidget(
        id = "widget_weather",
        type = WidgetType.WEATHER,
        pageIndex = 0,
        colSpan = 2,
        rowSpan = 1,
        order = 1
      ),
      LauncherWidget(
        id = "widget_pedometer",
        type = WidgetType.PEDOMETER,
        pageIndex = 0,
        colSpan = 2,
        rowSpan = 1,
        order = 2
      ),
      LauncherWidget(
        id = "widget_quick_toggles",
        type = WidgetType.QUICK_SETTINGS,
        pageIndex = 0,
        colSpan = 2,
        rowSpan = 2,
        order = 3
      ),
      LauncherWidget(
        id = "widget_analog_clock",
        type = WidgetType.ANALOG_CLOCK,
        pageIndex = 1,
        colSpan = 2,
        rowSpan = 2,
        order = 0
      ),
      LauncherWidget(
        id = "widget_voice_memo",
        type = WidgetType.VOICE_RECORDER,
        pageIndex = 1,
        colSpan = 2,
        rowSpan = 1,
        order = 1
      ),
      LauncherWidget(
        id = "widget_big_folder",
        type = WidgetType.BIG_FOLDER,
        pageIndex = 1,
        colSpan = 2,
        rowSpan = 2,
        order = 2
      )
    )
  }

  suspend fun addWidget(widget: LauncherWidget) = withContext(Dispatchers.IO) {
    dao.insertWidget(widget.toEntity())
  }

  suspend fun removeWidget(id: String) = withContext(Dispatchers.IO) {
    dao.deleteWidget(id)
  }

  suspend fun resetWidgetsToDefault() = withContext(Dispatchers.IO) {
    dao.clearWidgets()
    dao.insertWidgets(getDefaultWidgets().map { it.toEntity() })
  }

  suspend fun toggleFavorite(appId: String, current: Boolean) = withContext(Dispatchers.IO) {
    dao.updateAppMetadata(
      AppMetadataEntity(
        id = appId,
        isFavorite = !current
      )
    )
  }

  suspend fun toggleHidden(appId: String, current: Boolean) = withContext(Dispatchers.IO) {
    dao.updateAppMetadata(
      AppMetadataEntity(
        id = appId,
        isHidden = !current
      )
    )
  }

  suspend fun togglePinned(appId: String, current: Boolean) = withContext(Dispatchers.IO) {
    dao.updateAppMetadata(
      AppMetadataEntity(
        id = appId,
        isPinnedHome = !current
      )
    )
  }

  suspend fun updateAppCustomGlyph(appId: String, glyph: String) = withContext(Dispatchers.IO) {
    dao.updateAppMetadata(
      AppMetadataEntity(
        id = appId,
        customGlyph = glyph
      )
    )
  }

  // Settings
  fun getSettings(): Flow<LauncherSettings> {
    return dao.getSettings().map { entity ->
      if (entity == null) {
        LauncherSettings()
      } else {
        LauncherSettings(
          iconTheme = try { IconTheme.valueOf(entity.iconTheme) } catch (e: Exception) { IconTheme.MONOCHROME },
          iconShape = try { com.example.model.IconShape.valueOf(entity.iconShape) } catch (e: Exception) { com.example.model.IconShape.CIRCLE },
          iconScale = entity.iconScale,
          showAppLabels = entity.showAppLabels,
          wallpaperStyle = try { WallpaperStyle.valueOf(entity.wallpaperStyle) } catch (e: Exception) { WallpaperStyle.AMOLED_BLACK },
          wallpaperDim = entity.wallpaperDim,
          homeGridColumns = entity.homeGridColumns,
          drawerLayoutMode = try { DrawerLayoutMode.valueOf(entity.drawerMode) } catch (e: Exception) { DrawerLayoutMode.GRID_4 },
          dockStyle = try { com.example.model.DockStyle.valueOf(entity.dockStyle) } catch (e: Exception) { com.example.model.DockStyle.GLASS_PILL },
          dockAppCount = entity.dockAppCount,
          customDockAppIds = if (entity.customDockAppIds.isNotEmpty()) entity.customDockAppIds.split(",") else emptyList(),
          hapticFeedback = entity.hapticFeedback,
          doubleTapToSleep = entity.doubleTapToSleep,
          swipeDownForGlance = entity.swipeDownForGlance,
          glyphLightEffect = entity.glyphLightEffect,
          glyphMeterEnabled = entity.glyphMeterEnabled,
          customMarqueeText = entity.customMarqueeText,
          accentMode = try { com.example.model.AccentMode.valueOf(entity.accentMode) } catch (e: Exception) { com.example.model.AccentMode.NOTHING_RED }
        )
      }
    }
  }

  suspend fun updateSettings(settings: LauncherSettings) = withContext(Dispatchers.IO) {
    dao.saveSettings(
      SettingsEntity(
        id = 1,
        iconTheme = settings.iconTheme.name,
        iconShape = settings.iconShape.name,
        iconScale = settings.iconScale,
        showAppLabels = settings.showAppLabels,
        wallpaperStyle = settings.wallpaperStyle.name,
        wallpaperDim = settings.wallpaperDim,
        homeGridColumns = settings.homeGridColumns,
        drawerMode = settings.drawerLayoutMode.name,
        dockStyle = settings.dockStyle.name,
        dockAppCount = settings.dockAppCount,
        customDockAppIds = settings.customDockAppIds.joinToString(","),
        hapticFeedback = settings.hapticFeedback,
        doubleTapToSleep = settings.doubleTapToSleep,
        swipeDownForGlance = settings.swipeDownForGlance,
        glyphLightEffect = settings.glyphLightEffect,
        glyphMeterEnabled = settings.glyphMeterEnabled,
        customMarqueeText = settings.customMarqueeText,
        accentMode = settings.accentMode.name
      )
    )
  }

  private fun WidgetEntity.toModel(): LauncherWidget {
    val type = try {
      WidgetType.valueOf(typeName)
    } catch (e: Exception) {
      WidgetType.DIGITAL_CLOCK
    }
    return LauncherWidget(
      id = id,
      type = type,
      pageIndex = pageIndex,
      colSpan = colSpan,
      rowSpan = rowSpan,
      order = orderIndex,
      customTitle = customTitle
    )
  }

  private fun LauncherWidget.toEntity(): WidgetEntity {
    return WidgetEntity(
      id = id,
      typeName = type.name,
      pageIndex = pageIndex,
      colSpan = colSpan,
      rowSpan = rowSpan,
      orderIndex = order,
      customTitle = customTitle
    )
  }
}
