package com.example.strobokit.views

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.strobokit.R
import com.example.strobokit.composables.CarModel
import com.example.strobokit.composables.FeatureBox
import com.example.strobokit.composables.SettingsDialog
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.ui.theme.SecondaryColor
import com.example.strobokit.utilities.ChangeOrientationToPortrait
import com.example.strobokit.viewModels.BleDeviceDetailViewModel
import com.st.blue_sdk.models.NodeState

@SuppressLint("MissingPermission")
@Composable 
fun DeviceDetail(
    viewModel : BleDeviceDetailViewModel,
    navController: NavController,
    deviceId: String
){
    ChangeOrientationToPortrait(context = LocalContext.current)

    val bleDevice = viewModel.bleDevice(deviceId = deviceId).collectAsState(initial = null)
    val features = viewModel.features.collectAsState()

    if(bleDevice.value?.connectionStatus?.current == NodeState.Ready){
        viewModel.getFeatures(deviceId = deviceId)
    }

    val backHandlingEnabled by remember { mutableStateOf(true) }

    BackHandler(enabled = backHandlingEnabled) {
            viewModel.disconnect(deviceId = deviceId)
            navController.popBackStack()
    }

    val scrollState = rememberScrollState()
    var showDialog by remember { mutableStateOf(false) }

    val backgroundGradient = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to PrimaryColor,
            1f to PrimaryColor.copy(alpha = 0.6f)
        ),
        startY = 0.0f,
        endY = 1500.0f
    )
    Column(
        modifier = Modifier
            .fillMaxSize()

    ){
        //top Column
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .background(Color.LightGray)
            .background(backgroundGradient)
        ) {
            //top bar column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ){
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                            viewModel.disconnect(deviceId = deviceId)
                            navController.popBackStack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = OnPrimary)
                    }

                    Text(text = "Robot Menu", fontSize = 25.sp,color = OnPrimary, fontWeight = FontWeight.SemiBold)

                    IconButton(onClick = {
                        showDialog = true
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Back",tint = OnPrimary)
                    }
                    if (showDialog){
                        SettingsDialog(navController = navController, onDismiss = {showDialog = false},deviceId)
                    }
                }
            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CarModel()
                }

                Row(modifier = Modifier
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.BatteryFull, contentDescription = "Back", tint = SecondaryColor)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Thermostat, contentDescription = "Back", tint = SecondaryColor)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.SignalCellularAlt, contentDescription = "Back", tint = SecondaryColor)
                    }
                }
            }
        }

        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.LightGray)
            .background(backgroundGradient)
        ){

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Features", fontSize = 20.sp, color = OnPrimary, fontWeight = FontWeight.SemiBold)

                Column(modifier = Modifier
                    .fillMaxWidth()
                ) {

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scrollable(
                                state = scrollState,
                                orientation = Orientation.Horizontal
                            ),
                    ) {
                        val items = features.value.filter { it.isDataNotifyFeature }
                        val itemNames =  listOf("Remote Control") + items.map { it.name }
//                        val itemNames = listOf("Remote Control", "Follow Me", "Plot Data") + items.map { it.name }

                        itemsIndexed(items = itemNames) { index, item ->
                            Log.d("Device Detail",item)
                            if(item == "Switch" || item == "Accelerometer" || item == "Remote Control"){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (item == "Remote Control") {
                                                navController.navigate("feature/${deviceId}/controller")
                                            } else if (item == "Follow Me") {
                                                navController.navigate("feature/${deviceId}/controller")
                                            } else if (item == "Plot Data") {
                                                navController.navigate("feature/${deviceId}/controller")
                                            } else if (item == "Switch") {
                                                navController.navigate("feature/${deviceId}/${item}")
                                            } else {
                                                navController.navigate("feature/${deviceId}/plot")
                                            }
                                        }
                                ) {
                                    FeatureBox(item)
                                }
                            }

                        }
                    }
                }

                Row(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "@ 2024 Robotics Inc.",color = OnPrimary)
                    Text(text = "Version 1.0.0",color = OnPrimary)
                }
            }
        }
    }
}

data class Feature(val name: String, val isDataNotifyFeature: Boolean)


@Preview
@Composable
fun DeviceDetailPreview(){
    val backgroundGradient = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to PrimaryColor,  // Start with PrimaryColor
            1f to PrimaryColor.copy(alpha = 0.6f)      // Transition to White at the bottom-right corner
        ),
        startY = 0.0f,
        endY = 1500.0f
    )
    val scrollState = rememberScrollState()
    val sampleFeatures = remember {
        mutableStateOf(
            listOf(
                Feature("Feature A", true),
                Feature("Feature B", true),
                Feature("Feature A", true),
                Feature("Feature B", true),
                Feature("Feature A", true),
                Feature("Feature B", true),
            )
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()

    ){
        //top Column
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .background(Color.LightGray)
            .background(backgroundGradient)
        ) {
            //top bar column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ){
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = OnPrimary)
                    }

                    Text(text = "Robot Menu", fontSize = 25.sp,color = OnPrimary, fontWeight = FontWeight.SemiBold)

                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Settings, contentDescription = "Back",tint = OnPrimary)
                    }
                }
            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.robo_car),
                        contentDescription = "Your Image Description",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .size(200.dp)
                    )
                }

                Row(modifier = Modifier
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.BatteryFull, contentDescription = "Back", tint = SecondaryColor)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Thermostat, contentDescription = "Back", tint = SecondaryColor)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.SignalCellularAlt, contentDescription = "Back", tint = SecondaryColor)
                    }
                }
            }
        }

        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.LightGray)
            .background(backgroundGradient)
        ){

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Features", fontSize = 20.sp, color = OnPrimary, fontWeight = FontWeight.SemiBold)

                //column for options (LazyRender to be done later)
                Column(modifier = Modifier
                    .fillMaxWidth()
                ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .scrollable(
                                    state = scrollState,
                                    orientation = Orientation.Horizontal // Changed to Horizontal
                                ),
                        ) {
                            val items = sampleFeatures.value.filter { it.isDataNotifyFeature }
                            val itemNames = listOf("Remote Control", "Follow Me", "Plot Data") + items.map { it.name }

                            itemsIndexed(items = itemNames) { index, item ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {}
                                ) {
                                    FeatureBox(item)
                                }
                            }
                        }
                }

                Row(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "@ 2024 Robotics Inc.",color = OnPrimary)
                    Text(text = "Version 1.0.0",color = OnPrimary)
                }
            }
        }
    }
}

