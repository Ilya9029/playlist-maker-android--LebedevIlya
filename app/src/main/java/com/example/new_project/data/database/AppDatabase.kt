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
    version = 3,  // ✅ ИЗМЕНЕНО: увеличили версию с 2 на 3
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

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE playlists ADD COLUMN cover_image_uri TEXT")
            }
        }

        // ✅ НОВАЯ миграция с версии 2 на 3
        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. Создаем новую таблицу с правильным именем колонки
                database.execSQL("""
                    CREATE TABLE playlists_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        cover_image_path TEXT,
                        created_at INTEGER NOT NULL
                    )
                """.trimIndent())

                // 2. Копируем данные из старой таблицы (переименовываем колонку)
                database.execSQL("""
                    INSERT INTO playlists_new (id, name, description, cover_image_path, created_at)
                    SELECT id, name, description, cover_image_uri, created_at 
                    FROM playlists
                """.trimIndent())

                // 3. Удаляем старую таблицу
                database.execSQL("DROP TABLE playlists")

                // 4. Переименовываем новую таблицу
                database.execSQL("ALTER TABLE playlists_new RENAME TO playlists")
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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