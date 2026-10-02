package com.example.model

enum class IconTheme(val label: String, val desc: String) {
  MONOCHROME("Nothing Monochrome", "Pure high-contrast black & white glyphs"),
  MONOCHROME_RED("Red Accent Glyph", "Monochrome glyphs with signature Nothing red dot"),
  INVERTED("Paper White", "Inverted white badges with obsidian icons")
}

enum class IconShape(val label: String) {
  CIRCLE("Circle"),
  SQUIRCLE("Squircle"),
  OCTAGON("Octagon"),
  ROUNDED_SQUARE("Square")
}

enum class DockStyle(val label: String) {
  GLASS_PILL("Glass Pill"),
  FLOATING_MINIMAL("Floating Minimal"),
  DOTTED_BORDER("Dot Matrix Border"),
  HIDDEN("Hidden Dock")
}

enum class AccentMode(val label: String, val hexColor: Long) {
  NOTHING_RED("Signature Red", 0xFFD71921),
  PURE_WHITE("Studio White", 0xFFFFFFFF),
  AMBER_CYBER("Amber Glow", 0xFFFF9500),
  MATRIX_GREEN("Matrix Cyan", 0xFF00E5FF)
}

enum class WallpaperStyle(val label: String) {
  AMOLED_BLACK("AMOLED Pure Black"),
  NOTHING_ABSTRACT("Nothing Glass Ribbons"),
  DOT_GRID_MATRIX("Micro Dot Matrix"),
  MINIMAL_LINES("Minimal Wireframe"),
  MATRIX_DIGITAL("Digital Rain Noise"),
  RED_HORIZON("Dark Red Horizon")
}

enum class DrawerLayoutMode(val label: String, val columns: Int) {
  GRID_4("Grid (4 Columns)", 4),
  GRID_3("Spacious (3 Columns)", 3),
  COMPACT_LIST("Alphabetical List", 1)
}

data class LauncherSettings(
  val iconTheme: IconTheme = IconTheme.MONOCHROME,
  val iconShape: IconShape = IconShape.CIRCLE,
  val iconScale: Float = 0.9f, // Compact by default
  val showAppLabels: Boolean = true,
  val wallpaperStyle: WallpaperStyle = WallpaperStyle.AMOLED_BLACK,
  val wallpaperDim: Float = 0.35f,
  val homeGridColumns: Int = 4,
  val drawerLayoutMode: DrawerLayoutMode = DrawerLayoutMode.GRID_4,
  val dockStyle: DockStyle = DockStyle.GLASS_PILL,
  val dockAppCount: Int = 4,
  val customDockAppIds: List<String> = emptyList(),
  val hapticFeedback: Boolean = true,
  val doubleTapToSleep: Boolean = true,
  val swipeDownForGlance: Boolean = true,
  val showDotDividers: Boolean = true,
  val glyphLightEffect: Boolean = true,
  val glyphMeterEnabled: Boolean = true,
  val customMarqueeText: String = "NOTHING OS // NEVER SETTLE",
  val accentMode: AccentMode = AccentMode.NOTHING_RED
)
