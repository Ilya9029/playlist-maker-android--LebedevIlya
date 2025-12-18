package com.example.new_project.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.new_project.R
import androidx.core.net.toUri

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isDarkTheme by remember { mutableStateOf(false) }

    // ✅ ПОЛУЧАЕМ СТРОКИ ЗАРАНЕЕ (вне onClick)
    val shareUrl = stringResource(R.string.share_app_url)
    val supportEmail = stringResource(R.string.support_email)
    val emailSubject = stringResource(R.string.support_email_subject)
    val emailBody = stringResource(R.string.support_email_body)
    val agreementUrl = stringResource(R.string.user_agreement_url)

    MaterialTheme {
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
                        contentDescription = "Назад",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onBack() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Настройки",
                        fontSize = 22.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Белая карточка
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
                ) {
                    SettingsItemWithSwitch(
                        title = stringResource(R.string.dark_theme),
                        isChecked = isDarkTheme,
                        onCheckedChange = { checked -> isDarkTheme = checked },
                        topOffset = 8.dp
                    )

                    SettingsItem(
                        title = stringResource(R.string.share_app),
                        icon = Icons.Filled.Share,
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareUrl)  // ✅ Используем предварительно полученную строку
                            }
                            context.startActivity(
                                Intent.createChooser(shareIntent, null)
                            )
                        }
                    )

                    SettingsItem(
                        title = stringResource(R.string.write_support),
                        icon = Icons.Filled.Email,
                        onClick = {
                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:")
                                putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))  // ✅ Используем предварительно полученную строку
                                putExtra(Intent.EXTRA_SUBJECT, emailSubject)        // ✅ Используем предварительно полученную строку
                                putExtra(Intent.EXTRA_TEXT, emailBody)              // ✅ Используем предварительно полученную строку
                            }
                            try {
                                context.startActivity(emailIntent)
                            } catch (e: Exception) {
                                // почтового клиента нет — ничего не делаем
                            }
                        }
                    )

                    SettingsItem(
                        title = stringResource(R.string.user_agreement),
                        icon = Icons.Outlined.Description,
                        onClick = {
                            val browserIntent = Intent(
                                Intent.ACTION_VIEW,
                                agreementUrl.toUri()  // ✅ Используем предварительно полученную строку
                            )
                            context.startActivity(browserIntent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsItemWithSwitch(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    topOffset: Dp = 0.dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topOffset)
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
        )

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF2962FF),
                checkedTrackColor = Color(0xFF2962FF).copy(alpha = 0.5f),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.LightGray.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
        )

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}