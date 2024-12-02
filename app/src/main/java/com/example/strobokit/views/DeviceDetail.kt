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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Battery2Bar
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.example.strobokit.ui.theme.ErrorColor
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.ui.theme.SecondaryColor
import com.example.strobokit.ui.theme.SuccessColor
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

    val batteryData by viewModel.batteryData.collectAsState(initial = null)
    val batteryPercentage = batteryData?.percentage?.value?.toInt()

    val rssiData : String = bleDevice.value?.rssi?.rssi.toString()

    var isFeaturesFetched by remember { mutableStateOf(false) }

    if(bleDevice.value?.connectionStatus?.current == NodeState.Ready && !isFeaturesFetched){
        viewModel.getFeatures(deviceId = deviceId)
        isFeaturesFetched = true
    }

    val backHandlingEnabled by remember { mutableStateOf(true) }

    BackHandler(enabled = backHandlingEnabled) {
        viewModel.disconnect(deviceId = deviceId)
        navController.popBackStack()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.disableFeatures(deviceId = deviceId)
        }
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            viewModel.disconnect(deviceId = deviceId)
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = OnPrimary)
                        }
                    }

                    Text(
                        text = "Dashboard",
                        fontSize = 25.sp,
                        color = OnPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Center)
                    )
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = {},modifier = Modifier.size(20.dp)) {
                            Log.d("Device detail",batteryPercentage.toString())
                            Log.d("battery","$batteryData")
                            Log.d("battery","${batteryData?.status}")
                            if (batteryPercentage != null) {
                                if(batteryPercentage > 20) {
                                    Icon(Icons.Filled.BatteryFull, contentDescription = "batteryGood", tint = SuccessColor)
                                }else{
                                    Icon(Icons.Filled.Battery2Bar, contentDescription = "BatterLow", tint = ErrorColor)
                                }
                            }else{
                                Icon(Icons.Filled.BatteryFull, contentDescription = "batteryGood", tint = SecondaryColor)
                            }
                        }

                        if (batteryPercentage != null) {
                            if(batteryPercentage > 20) {
                                Text(text = "OK", color = OnPrimary, fontSize = 10.sp)
                            }else{
                                Text(text = "LOW",color = OnPrimary, fontSize = 10.sp)
                            }
                        }else{
                            Text(text = "NA",color = OnPrimary, fontSize = 10.sp)
                        }

                    }


                    Column(horizontalAlignment = Alignment.CenterHorizontally){
                        IconButton(onClick = {},modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Filled.SignalCellularAlt, contentDescription = "RSSI", tint = SecondaryColor)
                        }
                        Text(text = "$rssiData dBm",color = OnPrimary, fontSize = 10.sp)
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
                verticalArrangement = Arrangement.Top
            ) {
                Text(text = "Features", fontSize = 25.sp, color = OnPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(30.dp))
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val items = features.value
                        items.map { item->
                            Log.d("FEATURES",item.name)
                        }
//                        val itemNames =  listOf("Remote Control","Plot Data","Switch") + items.map { it.name }
//                        val itemNames =  listOf("Remote Control","Plot Data","Debug","Free navigation","Follow me","Edge detection")
                        val itemNames =  listOf("Remote Control","Plot Data","Debug","Algorithm selection")
//                        val itemNames = listOf("Remote Control", "Follow Me", "Plot Data") + items.map { it.name }

                        itemsIndexed(items = itemNames) { _, item ->
//                            Log.d("Device Detail",item)
                            if(item == "Switch" || item == "Plot Data" || item == "Remote Control" || item == "Debug" || item == "Algorithm selection"){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            when (item) {
                                                "Remote Control" -> {
                                                    val batteryValue = batteryPercentage ?: -1
                                                    navController.navigate("feature/${deviceId}/controller/${batteryValue}")
                                                }
                                                "Plot Data" -> {
                                                    navController.navigate("feature/${deviceId}/plot")
                                                }
                                                "Debug" -> {
                                                    navController.navigate("feature/${deviceId}/debugConsole")
                                                }
                                                "Algorithm selection" -> {
                                                    navController.navigate("feature/${deviceId}/algorithms")
                                                }
                                            }
                                        }
                                ) {
                                    FeatureBox(item)
                                }
                            }

                        }
                    }
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
            0.0f to PrimaryColor,
            1f to PrimaryColor.copy(alpha = 0.6f)
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

                        itemsIndexed(items = itemNames) { _, item ->
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

