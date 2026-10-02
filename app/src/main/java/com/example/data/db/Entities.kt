package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "widgets")
data class WidgetEntity(
  @PrimaryKey val id: String,
  val typeName: String,
  val pageIndex: Int,
  val colSpan: Int,
  val rowSpan: Int,
  val orderIndex: Int,
  val customTitle: String = ""
)

@Entity(tableName = "app_metadata")
data class AppMetadataEntity(
  @PrimaryKey val id: String, // packageName/activityName
  val isFavorite: Boolean = false,
  val isHidden: Boolean = false,
  val isPinnedHome: Boolean = false,
  val customCategory: String = "",
  val customGlyph: String = "",
  val launchCount: Int = 0
)

@Entity(tableName = "launcher_settings")
data class SettingsEntity(
  @PrimaryKey val id: Int = 1,
  val iconTheme: String = "MONOCHROME",
  val iconShape: String = "CIRCLE",
  val iconScale: Float = 0.9f,
  val showAppLabels: Boolean = true,
  val wallpaperStyle: String = "AMOLED_BLACK",
  val wallpaperDim: Float = 0.35f,
  val homeGridColumns: Int = 4,
  val drawerMode: String = "GRID_4",
  val dockStyle: String = "GLASS_PILL",
  val dockAppCount: Int = 4,
  val customDockAppIds: String = "",
  val hapticFeedback: Boolean = true,
  val doubleTapToSleep: Boolean = true,
  val swipeDownForGlance: Boolean = true,
  val glyphLightEffect: Boolean = true,
  val glyphMeterEnabled: Boolean = true,
  val customMarqueeText: String = "NOTHING OS // NEVER SETTLE",
  val accentMode: String = "NOTHING_RED"
)
