package com.example.ui.home

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.db.LauncherDatabase
import com.example.model.AppInfo
import com.example.model.LauncherSettings
import com.example.model.LauncherWidget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LauncherViewModel(application: Application) : AndroidViewModel(application) {
  private val database = LauncherDatabase.getDatabase(application)
  private val repository = AppRepository(application, database.launcherDao())

  val apps: StateFlow<List<AppInfo>> = repository.getInstalledApps()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val widgets: StateFlow<List<LauncherWidget>> = repository.getWidgets()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val settings: StateFlow<LauncherSettings> = repository.getSettings()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LauncherSettings())

  private val _isDrawerOpen = MutableStateFlow(false)
  val isDrawerOpen: StateFlow<Boolean> = _isDrawerOpen.asStateFlow()

  private val _isQuickGlanceOpen = MutableStateFlow(false)
  val isQuickGlanceOpen: StateFlow<Boolean> = _isQuickGlanceOpen.asStateFlow()

  private val _isCustomizationOpen = MutableStateFlow(false)
  val isCustomizationOpen: StateFlow<Boolean> = _isCustomizationOpen.asStateFlow()

  private val _isSleeping = MutableStateFlow(false)
  val isSleeping: StateFlow<Boolean> = _isSleeping.asStateFlow()

  fun openDrawer() {
    _isDrawerOpen.value = true
  }

  fun closeDrawer() {
    _isDrawerOpen.value = false
  }

  fun openQuickGlance() {
    _isQuickGlanceOpen.value = true
  }

  fun closeQuickGlance() {
    _isQuickGlanceOpen.value = false
  }

  fun openCustomization() {
    _isCustomizationOpen.value = true
  }

  fun closeCustomization() {
    _isCustomizationOpen.value = false
  }

  fun sleepScreen() {
    _isSleeping.value = true
  }

  fun wakeScreen() {
    _isSleeping.value = false
  }

  fun launchApp(app: AppInfo) {
    val context = getApplication<Application>()
    val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
    if (intent != null) {
      try {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
      } catch (e: Exception) {
        Toast.makeText(context, "Opening ${app.label}", Toast.LENGTH_SHORT).show()
      }
    } else {
      // Demo / simulated apps
      Toast.makeText(context, "Launched ${app.label}", Toast.LENGTH_SHORT).show()
    }
  }

  fun togglePin(app: AppInfo) {
    viewModelScope.launch {
      repository.togglePinned(app.id, app.isPinnedHome)
    }
  }

  fun toggleFavorite(app: AppInfo) {
    viewModelScope.launch {
      repository.toggleFavorite(app.id, app.isFavorite)
    }
  }

  fun toggleHide(app: AppInfo) {
    viewModelScope.launch {
      repository.toggleHidden(app.id, app.isHidden)
    }
  }

  fun setCustomGlyph(appId: String, glyph: String) {
    viewModelScope.launch {
      repository.updateAppCustomGlyph(appId, glyph)
    }
  }

  fun addWidget(widget: LauncherWidget) {
    viewModelScope.launch {
      repository.addWidget(widget)
    }
  }

  fun removeWidget(id: String) {
    viewModelScope.launch {
      repository.removeWidget(id)
    }
  }

  fun resetWidgets() {
    viewModelScope.launch {
      repository.resetWidgetsToDefault()
    }
  }

  fun updateSettings(newSettings: LauncherSettings) {
    viewModelScope.launch {
      repository.updateSettings(newSettings)
    }
  }
}
