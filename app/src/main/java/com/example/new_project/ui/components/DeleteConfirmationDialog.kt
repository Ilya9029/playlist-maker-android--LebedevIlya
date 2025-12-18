package com.example.new_project.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.new_project.R

@Composable
fun DeleteConfirmationDialog(
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmDelete()
                    onDismissRequest()
                }
            ) {
                Text(text = "Удалить")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text(text = "Отмена")
            }
        }
    )
}

@Preview
@Composable
fun DeleteConfirmationDialogPreview() {
    DeleteConfirmationDialog(
        title = "Удалить из избранного",
        message = "Вы уверены, что хотите удалить этот трек из избранного?",
        onDismissRequest = {},
        onConfirmDelete = {}
    )
}