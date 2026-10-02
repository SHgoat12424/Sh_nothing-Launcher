package com.example.model

enum class AppCategory(val displayName: String) {
  ALL("All"),
  ESSENTIALS("Essentials"),
  TOOLS("Tools"),
  SOCIAL("Social"),
  MEDIA("Media"),
  GAMES("Games"),
  SYSTEM("System")
}

data class AppInfo(
  val id: String, // packageName/activityName or mock_id
  val packageName: String,
  val activityName: String,
  val label: String,
  val category: AppCategory = AppCategory.ESSENTIALS,
  val isFavorite: Boolean = false,
  val isHidden: Boolean = false,
  val isPinnedHome: Boolean = false,
  val iconGlyph: String = "app", // e.g. "camera", "phone", "chat", "browser", "settings", "music", "gallery", "clock", "calculator", "notes", "calendar", "files", "maps", "recorder", "weather"
  val launchCount: Int = 0
)
