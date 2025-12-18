package com.example.new_project

import android.app.Application
import com.example.new_project.creator.Creator

class PlaylistMakerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Инициализируем Creator с контекстом приложения
        Creator.init(this)
    }
}