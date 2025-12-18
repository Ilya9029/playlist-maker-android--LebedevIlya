package com.example.new_project.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
    version = 1,
    exportSchema = true  // Важно для миграций в будущем
)
@TypeConverters()  // Здесь можно добавить конвертеры для сложных типов
abstract class AppDatabase : RoomDatabase() {

    // ========== DAO ДОСТУП ==========
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao

    // ========== SINGLETON ПАТТЕРН ==========
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

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
                    "playlist_maker.db"  // Имя файла базы данных
                )
                    .fallbackToDestructiveMigration()  // Временное решение - удаляет базу при изменении версии
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