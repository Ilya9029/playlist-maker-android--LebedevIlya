package com.example.new_project.data.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Сохраняет изображение из URI во внутреннее хранилище приложения
 * @param context Контекст приложения
 * @param imageUri URI исходного изображения
 * @return Полный путь к сохраненному файлу или null в случае ошибки
 */
suspend fun saveImageToInternalStorage(
    context: Context,
    imageUri: Uri
): String? = withContext(Dispatchers.IO) {
    var inputStream: InputStream? = null
    var outputStream: FileOutputStream? = null

    try {
        // Создаем уникальное имя файла
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        val fileName = "cover_${timestamp}_${random}.jpg"

        // Получаем директорию для сохранения (внутреннее хранилище приложения)
        val internalDir = File(context.filesDir, "playlist_covers")
        if (!internalDir.exists()) {
            internalDir.mkdirs()
        }

        // Создаем файл для сохранения
        val imageFile = File(internalDir, fileName)

        // Открываем поток для чтения из URI
        inputStream = context.contentResolver.openInputStream(imageUri)

        // Читаем и декодируем изображение
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (bitmap != null) {
            // Сохраняем в формате JPEG
            outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()

            // Возвращаем полный путь к файлу
            return@withContext imageFile.absolutePath
        } else {
            return@withContext null
        }

    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext null
    } finally {
        // Закрываем потоки
        try {
            inputStream?.close()
            outputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * Загружает Bitmap из файла по пути
 */
fun loadBitmapFromPath(filePath: String): Bitmap? {
    return try {
        val file = File(filePath)
        if (file.exists()) {
            BitmapFactory.decodeFile(filePath)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Удаляет файл изображения по пути
 */
suspend fun deleteImageFile(filePath: String?): Boolean = withContext(Dispatchers.IO) {
    if (filePath.isNullOrEmpty()) return@withContext false

    return@withContext try {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        } else {
            false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * Проверяет, существует ли файл по указанному пути
 */
fun imageFileExists(filePath: String?): Boolean {
    if (filePath.isNullOrEmpty()) return false

    return try {
        val file = File(filePath)
        file.exists()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}