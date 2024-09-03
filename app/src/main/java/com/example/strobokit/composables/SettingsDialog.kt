package com.example.strobokit.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.TertiaryColor

@Composable
fun SettingsDialog(
    navController: NavController,
    onDismiss: () -> Unit,
    deviceId: String
) {
    AlertDialog(
        backgroundColor = OnPrimary,
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Please choose your option",
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        },
        buttons = {
            Column(
                modifier = Modifier.padding(all = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                androidx.compose.material3.Button(
                    onClick = {
                        navController.navigate("feature/${deviceId}/debugConsole")
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray.copy(alpha = 0.4f),
                        contentColor = TertiaryColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(text = "Debug Console")
                }
                androidx.compose.material3.OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OnPrimary,
                        contentColor = TertiaryColor
                    )
                ) {
                    Text(text = "Cancel")
                }
            }
        }
    )
}

@Preview
@Composable
fun SettingsDialogPreview() {
    AlertDialog(
        backgroundColor = OnPrimary,
        onDismissRequest = {},
        title = {
            Text(text = "Please choose your option",
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        },
        buttons = {
            Column(
                modifier = Modifier.padding(all = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {

                androidx.compose.material3.Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray.copy(alpha = 0.4f),
                        contentColor = TertiaryColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(text = "Debug Console", color = TertiaryColor)
                }
                androidx.compose.material3.OutlinedButton(
                    onClick = {  },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OnPrimary,
                        contentColor = TertiaryColor
                    )
                ) {
                    Text(text = "Cancel")
                }
            }
        }
    )
}
