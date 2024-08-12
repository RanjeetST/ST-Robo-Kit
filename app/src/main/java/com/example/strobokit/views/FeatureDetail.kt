package com.example.strobokit.views

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.ui.theme.TertiaryColor
import com.example.strobokit.viewModels.FeatureDetailViewModel
import com.st.blue_sdk.features.Feature

@Composable
fun FeatureDetail(
    navController: NavController,
    viewModel : FeatureDetailViewModel,
    deviceId: String,
    featureName: String
){
    val backHandlingEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.startCalibration(deviceId, featureName)
    }

    BackHandler(enabled = backHandlingEnabled) {
        viewModel.disconnectFeature(deviceId = deviceId, featureName = featureName)
        navController.popBackStack()
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ElevatedButton(
            onClick = { viewModel.sendExtendedCommand(featureName, deviceId, true) }
        ) {
            Text("Test Start")
        }

        ElevatedButton(
            onClick = { viewModel.sendExtendedCommand(featureName, deviceId, false) }
        ) {
            Text("Test Stop")
        }
    }

    LaunchedEffect(true) {
        viewModel.observeFeature(deviceId = deviceId, featureName = featureName)
        // viewModel.sendExtendedCommand(featureName = featureName, deviceId = deviceId, currentSwitchValue)
    }
}

@Composable
@Preview
fun FeatureDetailPreview(){

    Column(
        modifier = Modifier.fillMaxSize()
            .background(TertiaryColor)
    ) {
        ElevatedButton(
            onClick = {  }
        ) {
            Text("Test Start")
        }

        ElevatedButton(
            onClick = { }
        ) {
            Text("Test Stop")
        }
    }
}
