package com.example.strobokit.views

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.strobokit.R
import com.example.strobokit.composables.FeatureBox
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.utilities.ChangeOrientationToLandscape
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
    LaunchedEffect(key1 = deviceId){
        viewModel.connect(deviceId = deviceId)
    }

    val bleDevice = viewModel.bleDevice(deviceId = deviceId).collectAsState(initial = null)
    val features = viewModel.features.collectAsState()
    val connectionStatus = bleDevice.value?.connectionStatus?.current?.name?.uppercase()
    println(connectionStatus)

    if(bleDevice.value?.connectionStatus?.current == NodeState.Ready){
        viewModel.getFeatures(deviceId = deviceId)
    }

    val backHandlingEnabled by remember { mutableStateOf(true) }

    BackHandler(enabled = backHandlingEnabled) {
        viewModel.disconnect(deviceId = deviceId)
        navController.popBackStack()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OnPrimary)
    ){
        TopAppBar(
            title = { androidx.compose.material.Text("Device Features") },
            navigationIcon = {
                IconButton(onClick = {
                    viewModel.disconnect(deviceId = deviceId)
                    navController.popBackStack()
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            backgroundColor = PrimaryColor,
            contentColor = Color.White
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
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

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Features", fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor)
                    Column(
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("feature/${deviceId}/debugConsole")
                                    }
                                ,
                            ){
                                FeatureBox("Debug Console")
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("feature/${deviceId}/controller")
                                    }
                                ,
                            ){
                                FeatureBox("Controller")
                            }
                        }

                        LazyColumn(modifier = Modifier
                            .fillMaxWidth()
                            .scrollable(
                                state = scrollState,
                                orientation = Orientation.Vertical
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val items = features.value.filter { it.isDataNotifyFeature }
                            itemsIndexed(items = items) {index , item ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("feature/${deviceId}/${item.name}")
                                        }
                                    ,
                                ) {
                                    FeatureBox(item.name)
                                }
                            }
                        }
                    }
                }
            }
            androidx.compose.material.Text(
                "Name: ${bleDevice.value?.device?.name ?: ""}",
                style = MaterialTheme.typography.h4
            )

            Spacer(modifier = Modifier.height(4.dp))

            androidx.compose.material.Text(
                "Status: ${bleDevice.value?.connectionStatus?.current?.name?.uppercase() ?: ""}",
                style = MaterialTheme.typography.h5
            )

            Spacer(modifier = Modifier.height(4.dp))

            androidx.compose.material.Text("Features: ", style = MaterialTheme.typography.h5)
        }
    }
}

data class anyDevice(
    val name: String,
    val address: String
)

val someDevice = listOf(
    anyDevice(name = "Device 1", address = "00:11:22:33:44:55"),
    anyDevice(name = "Device 2", address = "66:77:88:99:AA:BB"),
)

@Preview
@Composable
fun DeviceDetailPreview(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OnPrimary)
    ){
        TopAppBar(
            title = { androidx.compose.material.Text("Device Features") },
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            backgroundColor = PrimaryColor,
            contentColor = Color.White
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
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

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Features", fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                            ,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { }
                                ,
                            ){
                                FeatureBox("Debug Console")
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { }
                                ,
                            ){
                                FeatureBox("Controller")
                            }

                        }

                        val scrollState = rememberScrollState()
                        LazyColumn(modifier = Modifier
                            .fillMaxWidth()
                            .scrollable(
                                state = scrollState,
                                orientation = Orientation.Vertical
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val items = someDevice
                            itemsIndexed(items = items) {index , item ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { }
                                    ,
                                ) {
                                    FeatureBox(item.name)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}