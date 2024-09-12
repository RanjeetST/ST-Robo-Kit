package com.example.strobokit.views

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TopAppBar
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.strobokit.composables.BluetoothChecker
import com.example.strobokit.composables.LocationChecker
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.viewModels.BleDeviceListViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.st.blue_sdk.models.NodeState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@SuppressLint("MissingPermission")
@Composable
fun BleDeviceList(viewModel: BleDeviceListViewModel, navController: NavController) {

    var doNotShowRationale by rememberSaveable { mutableStateOf(false) }
    val connectionState by viewModel.connectionState.collectAsState()

    val context = LocalContext.current

    val bluetoothManager = remember { context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager }
    val bluetoothAdapter = remember { bluetoothManager.adapter }

    val pullRefreshState = rememberPullRefreshState(refreshing = viewModel.isRefreshing, onRefresh = {
        if(bluetoothAdapter?.isEnabled == true){
            viewModel.onRefresh()
        }
    })

    val isScanning by viewModel.isLoading.collectAsStateWithLifecycle()

    val PermissionState = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            listOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    )

    val backgroundGradient = Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to PrimaryColor.copy(alpha = 0.9f),
            0.95f to PrimaryColor.copy(alpha = 0.7f),
            1f to PrimaryColor.copy(alpha = 0.6f)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    if (PermissionState.allPermissionsGranted) {
        LocationChecker()
        BluetoothChecker()
        LaunchedEffect(Unit) {
            if(bluetoothAdapter?.isEnabled == true)
            {
                viewModel.startScan()
            }
        }

        val bleDevices = viewModel.scanBleDevices.collectAsState(initial = emptyList())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .background(backgroundGradient)
        ) {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(align = Alignment.Center)
                    ) {
                        androidx.compose.material.Text("Devices")
                    }
                },
                backgroundColor = PrimaryColor,
                contentColor = Color.White
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    item {
                        Row(modifier = Modifier
                            .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(18.dp)
                                    .align(Alignment.CenterVertically),
                                strokeWidth = 2.dp,
                                color = OnPrimary
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            if(connectionState == NodeState.Disconnected){
                                if(isScanning)
                                {
                                    Text(text = "Scanning Devices", fontSize = 15.sp, color = OnPrimary)
                                }else{
                                    Text(text = "Scanning Stopped. Pull to rescan", fontSize = 15.sp, color = OnPrimary)
                                }

                            }else{
                                Text(text = "$connectionState", fontSize = 15.sp, color = OnPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }

                    itemsIndexed(items = bleDevices.value) { index, item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    viewModel.connect(item.device.address)
                                }
                        ) {
                            LaunchedEffect(connectionState) {
                                if(connectionState == NodeState.Connected)
                                {
                                    navController.navigate("detail/${item.device.address}")
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            modifier = Modifier.padding(4.dp),
                                            text = item.device.name,
                                            color = OnPrimary
                                        )
                                    }
                                    Text(
                                        modifier = Modifier.padding(4.dp),
                                        text = item.device.address,
                                        color = OnPrimary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                PullRefreshIndicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    refreshing = viewModel.isRefreshing,
                    state = pullRefreshState
                )
            }
        }
    } else {
        if (doNotShowRationale) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text("Feature not available")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
                    .background(backgroundGradient),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Please grant the permissions", color = Color.Black)

                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth(0.8f)) {
                    Button(
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor,
                            contentColor = OnPrimary
                        ),
                        modifier = Modifier.weight(0.5f),
                        onClick = {
                            PermissionState.launchMultiplePermissionRequest()
                        }
                    ) {
                        Text("Yes")
                    }

                    Spacer(Modifier.width(4.dp))

                    Button(
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor,
                            contentColor = OnPrimary
                        ),
                        modifier = Modifier.weight(0.5f),
                        onClick = { doNotShowRationale = true }
                    ) {
                        Text("No")
                    }
                }
            }
        }
    }
}


//***************** SAMPLE DATA AND PREVIEW FOR ABOVE ******************

data class BleDevice(
    val name: String,
    val address: String
)

val devices = listOf(
    BleDevice(name = "Device 1", address = "00:11:22:33:44:55"),
    BleDevice(name = "Device 2", address = "66:77:88:99:AA:BB"),
    BleDevice(name = "Device 3", address = "CC:DD:EE:FF:00:11"),
    BleDevice(name = "Device 4", address = "22:33:44:55:66:77"),
    BleDevice(name = "Device 5", address = "88:99:AA:BB:CC:DD")
)

@Preview
@Composable
fun BleDeviceListPreview(){

    val backgroundGradient = Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to PrimaryColor.copy(alpha = 0.9f),
            0.95f to PrimaryColor.copy(alpha = 0.7f),
            1f to PrimaryColor.copy(alpha = 0.6f)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .background(backgroundGradient)
    ) {
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(align = Alignment.Center)
                ) {
                    androidx.compose.material.Text("Devices")
                }
            },
            backgroundColor = PrimaryColor,
            contentColor = Color.White
        )

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Row {
                CircularProgressIndicator(modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.CenterVertically),
                    strokeWidth = 2.dp,
                    color = OnPrimary)
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Scanning Devices", fontSize = 15.sp, color = OnPrimary)
            }

            Spacer(modifier = Modifier.height(15.dp))

            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .weight(1f)
            ) {
                itemsIndexed(items = devices){index,item->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Column(modifier = Modifier.fillMaxWidth()){
                                Row(verticalAlignment = Alignment.CenterVertically){
                                    Text(
                                        modifier = Modifier.padding(4.dp),
                                        text = item.name,
                                        color = OnPrimary
                                    )
                                }
                                Text(
                                    modifier = Modifier.padding(4.dp),
                                    text = item.address,
                                    color = OnPrimary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
@Preview
fun PermissionBoxPreview(){
    val backgroundGradient = Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to PrimaryColor.copy(alpha = 0.9f),
            0.95f to PrimaryColor.copy(alpha = 0.7f),
            1f to PrimaryColor.copy(alpha = 0.6f)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .background(backgroundGradient)
        ,horizontalAlignment = Alignment.CenterHorizontally
        ,

        verticalArrangement = Arrangement.Center
    ) {
        Text("Please grant the permissions",color = OnPrimary)

        Spacer(modifier = Modifier.height(4.dp))

        Row(modifier = Modifier.fillMaxWidth(0.8f)) {
            Button(
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = OnPrimary
                ),
                modifier = Modifier.weight(0.5f),
                onClick = {}
            ) {
                Text("Yes")
            }

            Spacer(Modifier.width(4.dp))

            Button(
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = OnPrimary
                ),
                modifier = Modifier.weight(0.5f),
                onClick = { }
            ) {
                Text("No")
            }
        }
    }
}