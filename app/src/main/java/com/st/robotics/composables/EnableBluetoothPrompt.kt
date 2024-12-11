package com.st.robotics.composables

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.st.robotics.ui.theme.OnPrimary
import com.st.robotics.ui.theme.PrimaryColor


@Composable
fun EnableBluetoothPrompt(context: Context) {

    AlertDialog(
        onDismissRequest = {
            // Handle dismissal
        },
        title = {
            Text(text = "Bluetooth Required")
        },
        text = {
            Text(text = "This app requires Bluetooth to be enabled. Would you like to enable it now?")
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = PrimaryColor,
                    contentColor = OnPrimary
                ),
                onClick = {
                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                context.startActivity(intent)
            }) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = PrimaryColor,
                    contentColor = OnPrimary
                ),
                onClick = {
            }) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun EnableBluetoothPrompt() {

    AlertDialog(
        onDismissRequest = {
            // Handle dismissal
        },
        title = {
            Text(text = "Bluetooth Required")
        },
        text = {
            Text(text = "This app requires Bluetooth to be enabled. Would you like to enable it now?")
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = PrimaryColor,
                    contentColor = OnPrimary
                ),
                onClick = {
                }) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = PrimaryColor,
                    contentColor = OnPrimary
                ),
                onClick = {
                }) {
                Text("Cancel")
            }
        }
    )
}

