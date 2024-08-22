package com.example.strobokit.views

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.strobokit.R
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.ui.theme.TertiaryColor
import com.example.strobokit.viewModels.FeatureDetailViewModel
import com.st.blue_sdk.features.Feature
import com.st.blue_sdk.features.switchfeature.SwitchStatusType

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

    val features = viewModel.featureUpdates

    Column(
        modifier = Modifier.fillMaxSize()
            .background(TertiaryColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.st_feature_featureNameLabel, featureName))

        Spacer(modifier = Modifier.height(4.dp))

        Text(stringResource(R.string.st_feature_updatesLabel))

        Spacer(modifier = Modifier.height(4.dp))

        Text("${viewModel.featureUpdates.value}")

        Spacer(modifier = Modifier.height(8.dp))

        ElevatedButton(
            onClick = { viewModel.sendCommand(featureName, deviceId ,SwitchStatusType.Off) }
        ) {
            Text("Test Start")
        }

        ElevatedButton(
            onClick = { viewModel.sendCommand(featureName, deviceId,SwitchStatusType.On) }
        ) {
            Text("Test Stop")
        }
    }

    LaunchedEffect(true) {
        viewModel.observeFeature(deviceId = deviceId, featureName = featureName)
    }
}

@Composable
@Preview
fun FeatureDetailPreview(){

    Column(
        modifier = Modifier.fillMaxSize()
            .background(TertiaryColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Feature Name")

        Spacer(modifier = Modifier.height(4.dp))

        Text("Updates")

        Spacer(modifier = Modifier.height(4.dp))
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
