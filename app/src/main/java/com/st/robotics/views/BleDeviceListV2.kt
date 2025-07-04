package com.st.robotics.views

import MessageDialog
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.st.robotics.R
import com.st.robotics.composables.bluetoothChecker
import com.st.robotics.composables.locationChecker
import com.st.robotics.ui.theme.OnPrimary
import com.st.robotics.ui.theme.PrimaryColor
import com.st.robotics.viewModels.BleDeviceListViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.st.blue_sdk.models.Boards
import com.st.blue_sdk.models.NodeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@SuppressLint("MissingPermission")
@Composable
fun BleDeviceListV2(viewModel: BleDeviceListViewModel, navController: NavController) {

    var doNotShowRationale by rememberSaveable { mutableStateOf(false) }
    val connectionState by viewModel.connectionState.collectAsState()
    val pendingNavigationAddress by viewModel.pendingNavigationAddress.collectAsState()
    var hasNavigated by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var bluetoothManager by remember { mutableStateOf<BluetoothManager?>(null) }
    var bluetoothAdapter by remember { mutableStateOf<BluetoothAdapter?>(null) }

    val painter = painterResource(id = R.drawable.new_car)
    var isFiltered by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.cancelQrScan()
        }
    }

    LaunchedEffect(Unit) {
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager?.adapter
    }

    val pullRefreshState = rememberPullRefreshState(refreshing = viewModel.isRefreshing, onRefresh = {
        if(bluetoothAdapter?.isEnabled == true){
            viewModel.onRefresh()
        }
    })

    val dialogMessage by viewModel.dialogMessage.collectAsState()


    val isScanning by viewModel.isLoading.collectAsStateWithLifecycle()

    var connectedDeviceAddress by remember { mutableStateOf<String?>(null) }

    // CHECKS FOR REQUIRED PERMISSIONS BASED ON THE ANDROID BUILD VERSION
    val permissionsState = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_CONNECT,
            )
        } else {
            listOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    )


    if (permissionsState.allPermissionsGranted) {
        //TO PROMPT THE USER TO ENABLE LOCATION AND BLUETOOTH
        locationChecker()
        bluetoothChecker()
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
                .background(Color(0xFFF7F8FA))
        ) {
            if (dialogMessage != null) {
                MessageDialog(
                    message = dialogMessage!!,
                    onDismiss = { viewModel.dismissDialog() }
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Standard TopAppBar height
                color = OnPrimary,
                elevation = 4.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Back button - positioned at start
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryColor
                        )
                    }

                    // Title - perfectly centered on screen
                    androidx.compose.material.Text(
                        text = stringResource(id = R.string.pair_your_robot),
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryColor,
                        style = MaterialTheme.typography.h6

                    )

                    // Action buttons - positioned at end
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        IconButton(onClick = {
                            viewModel.scanQrCode(
                                onNodeFound = { address ->
                                    // Launch a coroutine to handle the delay
                                    CoroutineScope(Dispatchers.Main).launch {
                                        val isMainThread = Looper.myLooper() == Looper.getMainLooper()
                                        Log.d("BleDeviceListV2", " [MainThread: $isMainThread] [Thread: ${Thread.currentThread().name}]")

                                        // To ensure live variables are available
                                        delay(100)

                                        // Update the connected device address and connect
                                        connectedDeviceAddress = address
                                        viewModel.connect(address.toString())
                                    }
                                },
                            )
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.qr_code_scanner_24px),
                                contentDescription = "Scan QR Code",
                                tint = PrimaryColor
                            )
                        }

                        IconButton(onClick = {
                            isFiltered = !isFiltered
                            //viewModel.startScan(filter = isFiltered)
                        }) {
                            Icon(
                                painter = painterResource(
                                    id = if (isFiltered) {
                                        R.drawable.filter_list_24px // Original icon
                                    } else {
                                        R.drawable.filter_list_off_24px // Alternative icon
                                    }
                                ),
                                contentDescription = "Toggle Filter",
                                tint = PrimaryColor
                            )
                        }
                    }
                }
            }
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
                            if(isScanning){
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.CenterVertically),
                                    strokeWidth = 2.dp,
                                    color = PrimaryColor
                                )
                            }

                            Spacer(modifier = Modifier.width(5.dp))

                            if(connectionState == NodeState.Disconnected){
                                if(isScanning)
                                {
                                    Text(text = stringResource(id = R.string.looking_for_robots), fontSize = 15.sp, color = PrimaryColor)
                                }else{
                                    Text(text = stringResource(id = R.string.scanning_stopped), fontSize = 15.sp, color = PrimaryColor)
                                }

                            }else{
                                Text(text = "$connectionState", fontSize = 15.sp, color = PrimaryColor)
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(stringResource(id = R.string.detected_robots), color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Start)
                    }



                    itemsIndexed(items =
                        if(isFiltered){
                            bleDevices.value.filter { node ->
                                node.boardType == Boards.Model.STEVALROBKIT
                            }
                        }else{
                            bleDevices.value
                        }


                    ) { _, item ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    if (bluetoothAdapter?.isEnabled == true && isScanning) {
                                        viewModel.connect(item.device.address)
                                        connectedDeviceAddress = item.device.address
                                    } else if (bluetoothAdapter?.isEnabled == false) {
                                        Toast
                                            .makeText(
                                                context,
                                                "Bluetooth is turned off",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    } else {
                                        Toast
                                            .makeText(
                                                context,
                                                "Re-Scan to connect",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Image(painter = painter, contentDescription = "car", contentScale = ContentScale.Crop, modifier = Modifier.size(40.dp))
                                    Text(
                                        text = "${item.rssi?.rssi.toString()} dBm",
                                        color = PrimaryColor,
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.fillMaxWidth(0.8f)
                                , verticalArrangement = Arrangement.SpaceBetween) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(4.dp),
                                            text = item.device.name ?: "Unknown Device",
                                            color = PrimaryColor
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(4.dp),
                                            text = item.device.address ?: "N.A",
                                            color = PrimaryColor,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Surface(modifier = Modifier
                                    .clickable {
                                        if (bluetoothAdapter?.isEnabled == true && isScanning) {
                                            viewModel.connect(item.device.address)
                                            connectedDeviceAddress = item.device.address
                                        } else if (bluetoothAdapter?.isEnabled == false) {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Bluetooth is turned off",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        } else {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Re-Scan to connect",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                    }
                                    .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp)),
                                    color = PrimaryColor
                                ) {
                                    Text(stringResource(id = R.string.pair),color = OnPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 10.dp,vertical = 8.dp), textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
                LaunchedEffect(connectionState) {
                    if (connectionState == NodeState.Ready && !hasNavigated) {
                        hasNavigated = true
                        viewModel.cancelQrScan()
                        connectedDeviceAddress?.let {
                            navController.navigate("detail/$it")
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
                Text(stringResource(id = R.string.feature_not_available))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(stringResource(id = R.string.please_grant_permission), color = PrimaryColor)

                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth(0.8f)) {
                    Button(
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor,
                            contentColor = OnPrimary
                        ),
                        modifier = Modifier.weight(0.5f),
                        onClick = {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                    ) {
                        Text(stringResource(id = R.string.yes))
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
                        Text(stringResource(id = R.string.no))
                    }
                }
            }
        }
    }
}
