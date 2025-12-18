package com.example.new_project.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.new_project.data.database.dao.PlaylistDao
import com.example.new_project.data.database.dao.TrackDao
import com.example.new_project.data.database.entity.PlaylistEntity
import com.example.new_project.data.database.entity.PlaylistTrackCrossRef
import com.example.new_project.data.database.entity.TrackEntity

/**
 * Главная база данных приложения.
 * Содержит все таблицы и предоставляет доступ к DAO.
 */
@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class
    ],
    version = 2,  // ✅ ИЗМЕНЕНО: увеличили версию с 1 на 2
    exportSchema = true
)
@TypeConverters()
abstract class AppDatabase : RoomDatabase() {

    // ========== DAO ДОСТУП ==========
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao

    // ========== SINGLETON ПАТТЕРН ==========
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // ✅ НОВОЕ: миграция с версии 1 на 2
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Добавляем новую колонку cover_image_uri в таблицу playlists
                database.execSQL("ALTER TABLE playlists ADD COLUMN cover_image_uri TEXT")
            }
        }

        /**
         * Получает экземпляр базы данных (синглтон).
         * @param context контекст приложения
         * @return экземпляр AppDatabase
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "playlist_maker.db"
                )
                    .addMigrations(MIGRATION_1_2)  // ✅ ИЗМЕНЕНО: добавляем миграцию вместо деструктивной
                    .build()

                INSTANCE = instance
                instance
            }
        }

        /**
         * Получает экземпляр базы данных для тестов (in-memory).
         */
        fun getTestInstance(context: Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                AppDatabase::class.java
            ).build()
        }
    }
}