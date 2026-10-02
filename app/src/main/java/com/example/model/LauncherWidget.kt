package com.example.model

enum class WidgetType(val title: String, val description: String, val defaultWidthSpan: Int, val defaultHeightSpan: Int) {
  DIGITAL_CLOCK("Dot Matrix Clock", "Iconic Nothing LED digital time with date and battery", 2, 2),
  ANALOG_CLOCK("Minimal Analog Dial", "Circular watch dial with signature red second dot", 2, 2),
  WEATHER("Weather Glance", "Live temperature pill with dot-matrix weather glyph", 2, 1),
  QUICK_SETTINGS("Quick Controls", "Instant toggles for Torch, Wi-Fi, Sound, and Bluetooth", 2, 2),
  PEDOMETER("Active Screen & Steps", "Concentric Nothing activity rings", 2, 1),
  VOICE_RECORDER("Quick Tape Memo", "One-tap voice recorder with dot waveform", 2, 1),
  BIG_FOLDER("Oversized 2x2 Bubble", "Iconic Nothing circular folder with direct-launch apps", 2, 2),
  GLANCE_BAR("Nothing Glance", "Horizontal status bar with date, day & system state", 4, 1)
}

data class LauncherWidget(
  val id: String,
  val type: WidgetType,
  val pageIndex: Int = 0,
  val colSpan: Int = 2, // 1 to 4
  val rowSpan: Int = 2, // 1 to 2
  val order: Int = 0,
  val customTitle: String = ""
)
