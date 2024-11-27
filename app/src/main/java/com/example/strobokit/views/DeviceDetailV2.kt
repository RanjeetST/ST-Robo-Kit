package com.example.strobokit.views

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
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
fun DeviceDetailV2(
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

    val context = LocalContext.current

    val bluetoothManager = remember { context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager }
    val bluetoothAdapter = remember { bluetoothManager.adapter }


    if(bleDevice.value?.connectionStatus?.current == NodeState.Ready && !isFeaturesFetched && bluetoothAdapter.isEnabled){
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
//            viewModel.disableFeatures(deviceId = deviceId)
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
    val painter = painterResource(id = R.drawable.new_car)
    Column(
        modifier = Modifier
            .fillMaxSize()

    ){
        //top Column
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .background(OnPrimary)
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
                    Image(painter = painterResource(id = R.drawable.st_logo_clear), contentDescription = "ST_LOGO", modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                    ) {
                        IconButton(onClick = {
                            viewModel.disconnect(deviceId = deviceId)
                            navController.popBackStack()
                        }) {
                            Icon(painterResource(id = R.drawable.exit), contentDescription = "Back", tint = Color.Black)
                        }
                    }
                }
            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${bleDevice.value?.device?.name}", fontSize = 20.sp,color = PrimaryColor)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        if(bleDevice.value?.connectionStatus?.current == NodeState.Ready){
                            Text(text = "Connected",fontSize = 15.sp,color = Color.Black)
                        }else{
                            Text(text = "${bleDevice.value?.connectionStatus?.current}",fontSize = 15.sp,color = Color.Black)
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        if(bleDevice.value?.connectionStatus?.current == NodeState.Ready){
                            Dot(color = SuccessColor)
                        }else{
                            Dot(color = Color.Red)
                        }
                    }


                }

                Column(modifier = Modifier
                    .fillMaxWidth()
                    ,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

//                    CarModel()
                    Image(painter = painter, contentDescription = "car", contentScale = ContentScale.Crop, modifier = Modifier.size(200.dp))
//                    Image(painter = painterResource(id = R.drawable.new_car ), contentDescription = "car",Modifier.size(200.dp))
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .size(200.dp)
//                    ) {
//                        Image(
//                            painter = painter,
//                            contentDescription = "car",
//                            modifier = Modifier.size(200.dp),
//                            contentScale = ContentScale.Crop
//                        )
//                    }
                }

                Row(modifier = Modifier
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {},modifier = Modifier.size(20.dp)) {
                            if (batteryPercentage != null) {
                                if(batteryPercentage > 20) {
                                    Icon(painterResource(id = R.drawable.battery), contentDescription = "batteryGood", tint = SuccessColor)
                                }else{
                                    Icon(painterResource(id = R.drawable.battery), contentDescription = "BatterLow", tint = ErrorColor)
                                }
                            }else{
                                Icon(painterResource(id = R.drawable.battery), contentDescription = "batteryGood", tint = SecondaryColor)
                            }
                        }
                        Spacer(modifier = Modifier.width(1.dp))

                        if (batteryPercentage != null) {
                            if(batteryPercentage > 20) {
                                Text(text = "OK", color = PrimaryColor, fontSize = 10.sp)
                            }else{
                                Text(text = "LOW",color = PrimaryColor, fontSize = 10.sp)
                            }
                        }else{
                            Text(text = "NA",color = PrimaryColor, fontSize = 10.sp)
                        }

                    }


                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                        IconButton(onClick = {},modifier = Modifier.size(20.dp)) {
                            Icon(painterResource(id = R.drawable.wifi), contentDescription = "RSSI", tint = SecondaryColor)
                        }
                        Spacer(modifier = Modifier.width(1.dp))
                        Text(text = "$rssiData dBm",color = PrimaryColor, fontSize = 10.sp)
                    }
                }

                Column(modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Surface(modifier = Modifier
                        .clickable {
                            viewModel.sendCommand(
                                BleDeviceDetailViewModel.Commands.FREE_NAVIGATION,
                                deviceId
                            )
                        }
                        .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                        .fillMaxWidth(0.8f),
                        color = PrimaryColor
                    ) {
                        Text("Start Free Navigation",color = OnPrimary, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 12.dp), textAlign = TextAlign.Center)
                    }
                    Surface(modifier = Modifier
                        .clickable {
                            viewModel.sendCommand(
                                BleDeviceDetailViewModel.Commands.FOLLOW_ME,
                                deviceId
                            )
                        }
                        .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                        .fillMaxWidth(0.8f),
                        color = PrimaryColor
                    ) {
                        Text("Start Follow-me",color = OnPrimary, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 12.dp), textAlign = TextAlign.Center)
                    }
                    Surface(modifier = Modifier
                        .clickable {
                            viewModel.sendCommand(
                                BleDeviceDetailViewModel.Commands.REMOTE_CONTROL,
                                deviceId
                            )
                        }
                        .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                        .fillMaxWidth(0.8f),
                        color = PrimaryColor
                    ) {
                        Text("Start Remote Control",color = OnPrimary, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 12.dp), textAlign = TextAlign.Center)
                    }
                }
            }
        }

        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(PrimaryColor)
        ){

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .fillMaxHeight(),
                verticalArrangement = Arrangement.Top
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                ) {

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
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
                        val itemNames =  listOf("Home","Controller","Monitor","Debug")
//                        val itemNames = listOf("Remote Control", "Follow Me", "Plot Data") + items.map { it.name }

                        itemsIndexed(items = itemNames) { index, item ->
//                            Log.d("Device Detail",item)
                            if(item == "Home" || item == "Controller" || item == "Monitor" || item == "Debug"){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (item == "Controller") {
                                                val batteryValue = batteryPercentage ?: -1
                                                navController.navigate("feature/${deviceId}/controller/${batteryValue}")
                                            } else if (item == "Monitor") {
                                                navController.navigate("feature/${deviceId}/plot")
                                            } else if (item == "Debug") {
                                                navController.navigate("feature/${deviceId}/debugConsole")
                                            }
                                        }
                                ) {
                                    FeatureBox(item, iconSize = 25 , textSize = 10,textAreaSize = 45)
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Dot(color: Color) {
    Canvas(modifier = Modifier.size(10.dp)) {
        drawCircle(
            color = color,
            radius = size.minDimension / 2
        )
    }
}
