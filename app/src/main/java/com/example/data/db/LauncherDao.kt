package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LauncherDao {
  @Query("SELECT * FROM widgets ORDER BY pageIndex ASC, orderIndex ASC")
  fun getAllWidgets(): Flow<List<WidgetEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWidgets(widgets: List<WidgetEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWidget(widget: WidgetEntity)

  @Query("DELETE FROM widgets WHERE id = :id")
  suspend fun deleteWidget(id: String)

  @Query("DELETE FROM widgets")
  suspend fun clearWidgets()

  @Query("SELECT * FROM app_metadata")
  fun getAllAppMetadata(): Flow<List<AppMetadataEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun updateAppMetadata(metadata: AppMetadataEntity)

  @Query("SELECT * FROM launcher_settings WHERE id = 1 LIMIT 1")
  fun getSettings(): Flow<SettingsEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveSettings(settings: SettingsEntity)
}
