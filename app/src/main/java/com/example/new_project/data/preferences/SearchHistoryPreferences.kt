package com.example.new_project.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchHistoryPreferences(
    private val dataStore: DataStore<Preferences>,
    private val coroutineScope: CoroutineScope = CoroutineScope(
        CoroutineName("search-history-preferences") + SupervisorJob()
    )
) {
    private companion object {
        const val MAX_ENTRIES = 10
        const val SEPARATOR = ","
        val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")
    }

    /**
     * Добавляет запрос в историю поиска
     * @param word поисковый запрос
     */
    fun addEntry(word: String) {
        if (word.isEmpty()) {
            return
        }

        coroutineScope.launch {
            dataStore.edit { preferences ->
                val historyString = preferences[SEARCH_HISTORY_KEY].orEmpty()
                val history = if (historyString.isNotEmpty()) {
                    historyString.split(SEPARATOR).toMutableList()
                } else {
                    mutableListOf()
                }

                // Удаляем дубликат, если был
                history.remove(word)
                // Добавляем в начало (самый новый)
                history.add(0, word)

                // Ограничиваем количество записей
                val subList = if (history.size > MAX_ENTRIES) {
                    history.subList(0, MAX_ENTRIES)
                } else {
                    history
                }

                val updatedString = subList.joinToString(SEPARATOR)
                preferences[SEARCH_HISTORY_KEY] = updatedString
            }
        }
    }

    /**
     * Получает список запросов из истории
     * @return Flow со списком запросов (от новых к старым)
     */
    val searchHistory: Flow<List<String>> = dataStore.data.map { preferences ->
        val historyString = preferences[SEARCH_HISTORY_KEY].orEmpty()
        if (historyString.isEmpty()) {
            emptyList()
        } else {
            historyString.split(SEPARATOR)
        }
    }

    /**
     * Очищает историю поиска
     */
    suspend fun clearHistory() {
        dataStore.edit { preferences ->
            preferences.remove(SEARCH_HISTORY_KEY)
        }
    }

    /**
     * Получает историю поиска синхронно (для тестов)
     */
    suspend fun getEntries(): List<String> {
        return dataStore.data.map { preferences ->
            val historyString = preferences[SEARCH_HISTORY_KEY].orEmpty()
            if (historyString.isEmpty()) {
                emptyList()
            } else {
                historyString.split(SEPARATOR)
            }
        }.first()
    }
}