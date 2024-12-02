package com.example.strobokit.composables

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
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

@Composable
fun locationChecker() : Boolean {
    val context = LocalContext.current
    val locationManager = remember { context.getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    var showDialog by remember { mutableStateOf(!isLocationEnabled(context)) }

    LaunchedEffect(isLocationEnabled(context)) {
        showDialog = !isLocationEnabled(context)
    }

    if (showDialog) {
        AlertDialog(
            containerColor = OnPrimary,
            onDismissRequest = { showDialog = false },
            title = { Text("Enable Location Services") },
            text = { Text("Location services are turned off. Please enable location services to continue.") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    (context as? Activity)?.startActivity(intent)
                }
                    ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor
                    )) {
                    Text("Open Settings", color = OnPrimary)
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor
                        )
                    ) {
                    Text("Cancel",color = OnPrimary)
                }
            }
        )
    }

    return isLocationEnabled(context)
}

private fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}
