package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
  override fun migrate(db: SupportSQLiteDatabase) {
    try {
      db.execSQL("ALTER TABLE app_metadata ADD COLUMN customGlyph TEXT NOT NULL DEFAULT ''")
      db.execSQL("ALTER TABLE launcher_settings ADD COLUMN iconShape TEXT NOT NULL DEFAULT 'CIRCLE'")
      db.execSQL("ALTER TABLE launcher_settings ADD COLUMN iconScale REAL NOT NULL DEFAULT 0.9")
      db.execSQL("ALTER TABLE launcher_settings ADD COLUMN showAppLabels INTEGER NOT NULL DEFAULT 1")
      db.execSQL("ALTER TABLE launcher_settings ADD COLUMN wallpaperDim REAL NOT NULL DEFAULT 0.35")
      db.execSQL("ALTER TABLE launcher_settings ADD COLUMN dockStyle TEXT NOT NULL DEFAULT 'GLASS_PILL'")
      db.execSQL("ALTER TABLE launcher_settings ADD COLUMN dockAppCount INTEGER NOT NULL DEFAULT 4")
      db.execSQL("ALTER TABLE launcher_settings ADD COLUMN customDockAppIds TEXT NOT NULL DEFAULT ''")
      db.execSQL("ALTER TABLE launcher_settings ADD COLUMN glyphMeterEnabled INTEGER NOT NULL DEFAULT 1")
      db.execSQL("ALTER TABLE launcher_settings ADD COLUMN customMarqueeText TEXT NOT NULL DEFAULT 'NOTHING OS // NEVER SETTLE'")
      db.execSQL("ALTER TABLE launcher_settings ADD COLUMN accentMode TEXT NOT NULL DEFAULT 'NOTHING_RED'")
    } catch (e: Exception) {
      // Ignored if column already exists
    }
  }
}

@Database(
  entities = [WidgetEntity::class, AppMetadataEntity::class, SettingsEntity::class],
  version = 2,
  exportSchema = false
)
abstract class LauncherDatabase : RoomDatabase() {
  abstract fun launcherDao(): LauncherDao

  companion object {
    @Volatile
    private var INSTANCE: LauncherDatabase? = null

    fun getDatabase(context: Context): LauncherDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          LauncherDatabase::class.java,
          "nothing_launcher_v2.db"
        )
          .addMigrations(MIGRATION_1_2)
          .fallbackToDestructiveMigration(dropAllTables = true)
          .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
