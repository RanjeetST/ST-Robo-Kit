package com.example.strobokit.views

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.strobokit.composables.EnableBluetoothPrompt
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.ui.theme.SecondaryColor
import com.example.strobokit.viewModels.BleDeviceListViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun BleDeviceList(viewModel: BleDeviceListViewModel ,navController: NavController){

    var doNotShowRationale by rememberSaveable { mutableStateOf(false) }
//    var isBluetoothEnabled by rememberSaveable { mutableStateOf(false) }

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

//    val context = LocalContext.current
//    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//    val bluetoothAdapter = bluetoothManager.adapter
//    isBluetoothEnabled = bluetoothAdapter?.isEnabled == true

    if(PermissionState.allPermissionsGranted) {

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SecondaryColor)
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
                    .padding(16.dp)
                    .scrollable(state = scrollState, orientation = Orientation.Vertical),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Scanning Devices", fontSize = 20.sp,color = Color.Black)
                    Spacer(modifier = Modifier.height(6.dp))

                    val bleDevices = viewModel.scanBleDevices.collectAsState(initial = emptyList())

                    LazyColumn(modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                    ) {
                        itemsIndexed(items = bleDevices.value){index,item->
                            Surface(modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                                shape = RoundedCornerShape(4.dp),
                                shadowElevation = 10.dp,
                                onClick = {navController.navigate("detail/${item.device.address}")},
                                color = SecondaryColor,
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ){
                                    Column(modifier = Modifier.fillMaxWidth()){
                                        Row(verticalAlignment = Alignment.CenterVertically){
                                            Text(
                                                modifier = Modifier.padding(8.dp),
                                                text = item.device.name,
                                                color = Color.Black
                                            )
                                        }
                                        Text(
                                            modifier = Modifier.padding(8.dp),
                                            text = item.device.address,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
            }
            LaunchedEffect(Unit) {
                viewModel.startScan()
            }
    }else{
        if(doNotShowRationale){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text("Feature not available")
            }
        }else{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(60.dp),horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Please grant the permissions")

                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor,
                            contentColor = OnPrimary
                        ),
                        modifier = Modifier.weight(0.5f),
                        onClick = {PermissionState.launchMultiplePermissionRequest()}
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

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun BleDeviceListPreview(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecondaryColor)
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
            Text(text = "Scanning Devices", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .weight(1f)
//                .border(
//                    border = BorderStroke(2.dp, PrimaryColor)
//                )
            ) {
                itemsIndexed(items = devices){index,item->
                    Surface(modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                        shape = RoundedCornerShape(4.dp),
                        shadowElevation = 10.dp,
                        onClick = {},
                        color = SecondaryColor,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Column(modifier = Modifier.fillMaxWidth()){
                                Row(verticalAlignment = Alignment.CenterVertically){
                                    Text(
                                        modifier = Modifier.padding(8.dp),
                                        text = item.name
                                    )
                                }
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = item.address
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
@Preview(showBackground = true)
fun PermissionBoxPreview(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(60.dp),horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Please grant the permissions")

        Spacer(modifier = Modifier.height(4.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
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