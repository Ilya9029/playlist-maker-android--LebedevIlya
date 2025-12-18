Playlist Maker 🎵
Android приложение для создания музыкальных плейлистов с поиском через iTunes API. Реализовано на Kotlin с использованием современных технологий Android разработки.

📱 О проекте
Playlist Maker — это полнофункциональное Android приложение, позволяющее пользователям:

Искать музыку через iTunes API

Создавать и управлять персональными плейлистами

Добавлять обложки для плейлистов из галереи устройства

Сохранять любимые треки в избранное

Работать офлайн благодаря локальному хранению данных

Проект разработан в рамках курса Android-разработки Яндекс Практикум и демонстрирует применение современных подходов к разработке Android приложений.

✨ Ключевые возможности
🎵 Музыкальный поиск
Интеграция с iTunes Search API

История поиска с использованием DataStore

Автодополнение запросов

Отображение обложек альбомов в высоком качестве

📁 Управление плейлистами
Создание плейлистов с названием и описанием

Кастомизация обложек (выбор из галереи)

Добавление/удаление треков

Drag-and-drop сортировка (в планах)

⭐ Избранное
Отдельный экран для избранных треков

Быстрое добавление/удаление долгим нажатием

Локальное хранение состояния

🎨 Пользовательский интерфейс
Полностью реализован на Jetpack Compose

Material Design 3

Темная/светлая тема

Адаптивная верстка

🛠 Технологический стек
Языки и фреймворки
Kotlin — основной язык разработки

Jetpack Compose — декларативный UI фреймворк

Coroutines & Flow — асинхронное программирование

Kotlin DSL — конфигурация сборки

Архитектура
Clean Architecture — разделение на слои

MVVM — паттерн представления

Repository Pattern — абстракция доступа к данным

Dependency Injection — кастомная реализация

Локальное хранение
Room — реляционная база данных

DataStore — хранение настроек и истории

SharedPreferences — простые настройки

Сетевое взаимодействие
Retrofit 2 — HTTP клиент

Gson — сериализация JSON

Coil — загрузка и кэширование изображений

Навигация и UI
Compose Navigation — навигация между экранами

Material 3 — дизайн система

Accompanist — дополнительные компоненты Compose

📁 Структура проекта
app/
├── src/main/
│   ├── java/com/example/new_project/
│   │   ├── creator/           # Контейнер зависимостей
│   │   ├── domain/            # Доменный слой
│   │   │   ├── models/        # Бизнес-модели
│   │   │   └── interfaces/    # Контракты
│   │   ├── data/              # Слой данных
│   │   │   ├── dto/           # Data Transfer Objects
│   │   │   ├── database/      # Room компоненты
│   │   │   ├── network/       # Сетевой слой
│   │   │   ├── repository/    # Репозитории
│   │   │   └── preferences/   # Локальное хранение
│   │   └── ui/                # UI слой
│   │       ├── screens/       # Экраны приложения
│   │       ├── viewmodel/     # ViewModels
│   │       ├── navigation/    # Навигация
│   │       ├── components/    # Переиспользуемые компоненты
│   │       └── search/        # Компоненты поиска
│   └── res/                   # Ресурсы
└── build.gradle.kts           # Конфигурация сборки

🚀 Быстрый старт
Предварительные требования
Android Studio Giraffe (2022.3.1) или выше

Android SDK 29+

Java 17+

Интернет соединение для загрузки зависимостей

Установка
Клонируйте репозиторий
git clone https://github.com/yourusername/playlist-maker.git
cd playlist-maker

Откройте проект в Android Studio

Файл → Открыть → Выберите папку проекта

Дождитесь завершения синхронизации Gradle

Соберите проект
./gradlew assembleDebug

Запустите на эмуляторе или устройстве

Подключите устройство с включенной отладкой по USB

Или создайте эмулятор через AVD Manager

Нажмите Run (Shift+F10)

🏗️ Архитектура
Слоистая архитектура
UI Layer (Compose)
↓
ViewModel (StateFlow)
↓
Use Cases / Repositories
↓
Data Sources (API, Database)
↓
External APIs (iTunes)

Ключевые компоненты
Data Layer

Room DAO для локального хранения

Retrofit для сетевых запросов

DataStore для настроек

Domain Layer

Бизнес-модели (Track, Playlist)

Интерфейсы репозиториев

Use cases (в будущем)

UI Layer

Jetpack Compose экраны

ViewModels с StateFlow

Навигация через Compose Navigation

📱 Основные экраны
Главный экран
Навигационное меню

Быстрый доступ ко всем разделам

Современный Material Design

Поиск музыки
Поисковая строка с историей

Список результатов с обложками

Индикатор загрузки

Обработка ошибок

Создание плейлиста
Форма ввода названия и описания

Выбор обложки из галереи

Валидация данных

Material Design 3 компоненты

Избранное
Список избранных треков

Управление долгим нажатием

Синхронизация с локальной БД

Настройки
Переключение темы

Ссылки на поддержку

Пользовательское соглашение

🔧 Конфигурация
build.gradle.kts (app)
plugins {
id("com.android.application")
id("org.jetbrains.kotlin.android")
id("kotlin-kapt")
}

android {
namespace = "com.example.new_project"
compileSdk = 34

    defaultConfig {
        applicationId = "com.example.new_project"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
// Core
implementation("androidx.core:core-ktx:1.12.0")

    // Compose
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")
    
    // Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
}

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission 
        android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />

    <application
        android:name=".PlaylistMakerApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.PlaylistMaker">
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.PlaylistMaker">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>`

🧪 Тестирование
Типы тестов
Unit тесты для ViewModels и Use Cases

Интеграционные тесты для базы данных

UI тесты для основных сценариев

Запуск тестов
# Все тесты
./gradlew test

# Только unit тесты
./gradlew testDebugUnitTest

# Инструментальные тесты
./gradlew connectedDebugAndroidTest

Структура тестов
src/test/       # Unit тесты
src/androidTest/ # Инструментальные тесты

📈 Производительность
Оптимизации
LazyColumn для списков с большим количеством элементов

Coil с кэшированием изображений

Paging 3 для пагинации (в планах)

Room индексы для быстрого поиска

Мониторинг
Profiler для анализа использования CPU/памяти

Logcat с structured logging

Firebase Performance (в будущем)

🔄 Жизненный цикл разработки
Git workflow
main (production)
↑
develop (staging)
↑
feature/* (разработка)

Коммиты
Conventional Commits

Semantic versioning

CHANGELOG.md

CI/CD (в планах)
GitHub Actions

Автоматическое тестирование

Автодеплой в Google Play

📄 Документация
Внутренняя документация
KDoc для публичных API

README для модулей

Architecture Decision Records (ADR)

Внешняя документация
API документация (OpenAPI)

User guides

Contribution guidelines

🤝 Участие в разработке
Правила
Создайте issue для обсуждения изменений

Форкните репозиторий

Создайте feature branch

Добавьте тесты для новой функциональности

Сделайте Pull Request

Стиль кода
Kotlin Style Guide

Комментарии на английском

100% покрытие публичного API документацией

📞 Поддержка
Контакты
Автор: Илья Силаедр

Email: ilya.silaedr1@gmail.com

GitHub: @yourusername

Проект: https://github.com/yourusername/playlist-maker

Баги и улучшения
Используйте GitHub Issues

Включайте шаги для воспроизведения

Прикрепляйте скриншоты при необходимости

📜 Лицензия
MIT License

Copyright (c) 2024 Playlist Maker Contributors

Permission is hereby granted...

Полный текст лицензии: LICENSE

🎓 Обучение
Этот проект отлично подходит для изучения:

Jetpack Compose

Clean Architecture на Android

Работы с Room и Retrofit

Modern Android Development

Разработано в рамках курса Android-разработки Яндекс Практикум. Все права защищены.


