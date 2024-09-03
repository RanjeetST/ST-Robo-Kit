package com.example.strobokit.views

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.strobokit.R
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.ui.theme.TertiaryColor
import com.example.strobokit.viewModels.FeatureDetailViewModel
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
        modifier = Modifier
            .fillMaxSize()
            .background(TertiaryColor),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryColor)
        ){
            Row(modifier = Modifier
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                androidx.compose.material.IconButton(onClick = {navController.popBackStack()}) {
                    androidx.compose.material.Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = OnPrimary
                    )
                }
                Column(modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    androidx.compose.material3.Text(
                        text = "Test Screen",
                        fontSize = 20.sp,
                        color = OnPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(200.dp))
        Text(stringResource(R.string.st_feature_featureNameLabel, featureName))

        Spacer(modifier = Modifier.height(4.dp))

        Text(stringResource(R.string.st_feature_updatesLabel))

        Spacer(modifier = Modifier.height(4.dp))

        Text("${viewModel.featureUpdates.value}")

        Spacer(modifier = Modifier.height(8.dp))

        ElevatedButton(
            onClick = { viewModel.sendCommand(featureName, deviceId ,SwitchStatusType.Off) },
            colors = ButtonDefaults.buttonColors(
                containerColor = OnPrimary
            )
        ) {
            Text("Test Start")
        }

        ElevatedButton(
            onClick = { viewModel.sendCommand(featureName, deviceId,SwitchStatusType.On) },
            colors = ButtonDefaults.buttonColors(
                containerColor = OnPrimary
            )
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
        modifier = Modifier
            .fillMaxSize()
            .background(TertiaryColor),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryColor)
        ){
            Row(modifier = Modifier
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                androidx.compose.material.IconButton(onClick = {}) {
                    androidx.compose.material.Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = OnPrimary
                    )
                }
                Column(modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    androidx.compose.material3.Text(
                        text = "Test Screen",
                        fontSize = 20.sp,
                        color = OnPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(300.dp))

        Text("Feature Name")

        Spacer(modifier = Modifier.height(4.dp))

        Text("Updates")

        Spacer(modifier = Modifier.height(4.dp))
        ElevatedButton(
            onClick = {  },
            colors = ButtonDefaults.buttonColors(
                containerColor = OnPrimary
            )
        ) {
            Text("Test Start")
        }

        ElevatedButton(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = OnPrimary
            )
        ) {
            Text("Test Stop")
        }
    }
}
