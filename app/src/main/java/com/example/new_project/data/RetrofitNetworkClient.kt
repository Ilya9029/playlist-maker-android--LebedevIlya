package com.example.new_project.data

import android.util.Log
import com.example.new_project.data.network.api.ITunesApiService
import com.example.new_project.domain.BaseResponse
import com.example.new_project.domain.NetworkClient

class RetrofitNetworkClient(
    private val iTunesApiService: ITunesApiService
) : NetworkClient {

    override suspend fun doRequest(dto: Any): BaseResponse {
        Log.d("NetworkClient", "🚀 Начало запроса: ${dto::class.simpleName}")

        return try {
            when (dto) {
                is TracksSearchRequest -> {
                    Log.d("NetworkClient", "🔍 Поиск в iTunes: '${dto.expression}'")

                    val response = iTunesApiService.searchTracks(
                        term = dto.expression,
                        entity = dto.entity,
                        limit = dto.limit
                    )

                    Log.d("NetworkClient", "✅ Успех! Найдено треков: ${response.results.size}")
                    response
                }
                else -> {
                    Log.w("NetworkClient", "⚠️ Неподдерживаемый тип запроса")
                    BaseResponse(
                        resultCode = 400,
                        errorMessage = "Unsupported request type"
                    )
                }
            }
        } catch (e: Exception) {
            // 🔴 КРИТИЧЕСКО: логируем ВСЮ информацию об ошибке
            Log.e("NetworkClient", "❌ ИСКЛЮЧЕНИЕ в doRequest:", e)
            Log.e("NetworkClient", "❌ Тип ошибки: ${e.javaClass.name}")
            Log.e("NetworkClient", "❌ Сообщение: ${e.message}")
            e.printStackTrace() // Это ОЧЕНЬ важно для отладки!

            BaseResponse(
                resultCode = -1,
                errorMessage = "Network error: ${e.message}"
            )
        }
    }
}