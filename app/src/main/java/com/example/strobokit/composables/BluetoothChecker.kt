package com.example.strobokit.composables

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import androidx.compose.material.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.PrimaryColor
import kotlinx.coroutines.CoroutineScope

@SuppressLint("MissingPermission")
@Composable
fun BluetoothChecker() : Boolean{
    val context = LocalContext.current
    val bluetoothManager = remember { context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager }
    val bluetoothAdapter = remember { bluetoothManager.adapter }
    var showDialog by remember { mutableStateOf(bluetoothAdapter?.isEnabled == false) }

    LaunchedEffect(bluetoothAdapter?.isEnabled) {
        if (bluetoothAdapter?.isEnabled == true) {
            showDialog = false
        } else {
            showDialog = true
        }
    }

    if (showDialog) {
        AlertDialog(
            containerColor = OnPrimary,
            onDismissRequest = { showDialog = false },
            title = { Text("Enable Bluetooth") },
            text = { Text("Bluetooth is turned off. Please enable Bluetooth to continue.") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    (context as? Activity)?.startActivityForResult(enableBtIntent, 1)
                }
                    ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor
                    )) {
                    Text("Enable Bluetooth",color = OnPrimary)
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }
                    ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor
                    )) {
                    Text("Cancel", color = OnPrimary)
                }
            }
        )
    }
    return true
}
