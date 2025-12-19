package com.example.new_project.ui.screens

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.new_project.data.utils.loadBitmapFromPath
import com.example.new_project.data.utils.saveImageToInternalStorage
import com.example.new_project.ui.viewmodel.PlaylistsViewModel
import kotlinx.coroutines.launch

@Composable
fun NewPlaylistScreen(
    onBack: () -> Unit,
    viewModel: PlaylistsViewModel
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var coverImagePath by remember { mutableStateOf<String?>(null) }
    var previewBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Launcher для выбора изображения из галереи
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            // Сохраняем изображение во внутреннее хранилище
            scope.launch {
                val savedFilePath = saveImageToInternalStorage(context, selectedUri)
                if (savedFilePath != null) {
                    coverImagePath = savedFilePath
                    // Загружаем Bitmap для предпросмотра
                    previewBitmap = loadBitmapFromPath(savedFilePath)
                }
            }
        }
    }

    // Launcher для запроса разрешения на чтение файлов
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF2962FF)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Синяя шапка
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 16.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    "Новый плейлист",
                    fontSize = 22.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Белая карточка с содержимым
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp
                        )
                    )
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    "Создать плейлист",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ========== ВЫБОР ОБЛОЖКИ ==========
                Text(
                    text = "Обложка:",
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(205.dp)
                        .clickable {
                            // Для Android 13+ (API 33+) разрешения не нужны
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                imagePickerLauncher.launch("image/*")
                            } else {
                                // Для Android 12 и ниже проверяем разрешение
                                val permission = Manifest.permission.READ_EXTERNAL_STORAGE
                                when {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        permission
                                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                                        imagePickerLauncher.launch("image/*")
                                    }
                                    else -> {
                                        permissionLauncher.launch(permission)
                                    }
                                }
                            }
                        }
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (previewBitmap != null) {
                        // Показываем сохраненное изображение из файла
                        Image(
                            bitmap = previewBitmap!!.asImageBitmap(),
                            contentDescription = "Обложка плейлиста",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Показываем плейсхолдер (иконку добавления)
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Добавить обложку",
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
                // =====================================

                Spacer(modifier = Modifier.height(16.dp))

                // Поле для имени плейлиста
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Имя плейлиста") },
                    placeholder = { Text("Введите имя") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Поле для описания плейлиста
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    label = { Text("Описание плейлиста") },
                    placeholder = { Text("Введите описание (опционально)") },
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Кнопка сохранить
                Button(
                    onClick = {
                        // Проверяем, что имя не пустое
                        if (name.isNotBlank()) {
                            // ✅ ИЗМЕНЕНО: передаем путь к файлу, а не URI
                            viewModel.createNewPlayList(name, description, coverImagePath)
                            // Возвращаемся на экран плейлистов
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2962FF)
                    ),
                    enabled = name.isNotBlank()
                ) {
                    Text(
                        "Сохранить",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}