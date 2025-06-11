package com.st.robotics.views

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
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery4Bar
import androidx.compose.material.icons.filled.BatteryFull
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.st.blue_sdk.features.extended.ext_configuration.ExtConfiguration
import com.st.blue_sdk.models.NodeState
import com.st.robotics.R
import com.st.robotics.composables.FeatureBox
import com.st.robotics.ui.theme.ErrorColor
import com.st.robotics.ui.theme.OnPrimary
import com.st.robotics.ui.theme.PrimaryColor
import com.st.robotics.ui.theme.SecondaryColor
import com.st.robotics.ui.theme.SuccessColor
import com.st.robotics.utilities.ChangeOrientationToPortrait
import com.st.robotics.viewModels.BleDeviceDetailViewModel

@SuppressLint("MissingPermission")
@Composable
fun DeviceDetailV2(
    viewModel : BleDeviceDetailViewModel,
    navController: NavController,
    deviceId: String
){

    val bleDevice = viewModel.bleDevice(deviceId = deviceId).collectAsState(initial = null)
    val features = viewModel.features.collectAsState()

    val batteryData by viewModel.batteryData.collectAsState(initial = null)
    val batteryPercentage = batteryData?.percentage?.value?.toInt()
    val batteryVoltage = batteryData?.voltage?.value?.toFloat()

    val rssiData : String = bleDevice.value?.rssi?.rssi.toString()

    var isFeaturesFetched by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val bluetoothManager = remember { context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager }
    val bluetoothAdapter = remember { bluetoothManager.adapter }

    LaunchedEffect(bleDevice.value?.connectionStatus?.current, bluetoothAdapter.isEnabled) {
        val device = bleDevice.value
        if (device?.connectionStatus?.current == NodeState.Ready &&
            bluetoothAdapter.isEnabled &&
            !isFeaturesFetched
        ) {
            viewModel.getFeatures(deviceId = deviceId)
            //viewModel.getFwVersion(nodeId = deviceId, featureName = ExtConfiguration.NAME)
            isFeaturesFetched = true
        }
    }

    val backHandlingEnabled by remember { mutableStateOf(true) }

    val showDialog = rememberSaveable { mutableStateOf(true) }

    BackHandler(enabled = backHandlingEnabled) {
        viewModel.disconnect(deviceId = deviceId)
        navController.popBackStack()
    }

    LaunchedEffect(Unit) {
        ChangeOrientationToPortrait(context = context)
    }

    DisposableEffect(Unit) {
        onDispose {
            //viewModel.disableFeatures(deviceId = deviceId)
        }
    }


    val scrollState = rememberScrollState()

    Brush.verticalGradient(
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
        //Top Column
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
                    Text(text = "${bleDevice.value?.device?.name ?: ""}", fontSize = 20.sp,color = PrimaryColor)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){

                        if(bleDevice.value?.connectionStatus?.current == NodeState.Ready){
                            Text(text = stringResource(id = R.string.connected),fontSize = 15.sp,color = Color.Black)
                        }else {
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
                    Image(painter = painter, contentDescription = "car", contentScale = ContentScale.Crop, modifier = Modifier.size(280.dp))
                }

                Row(modifier = Modifier
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {},modifier = Modifier.size(20.dp)) {
                            Log.d("Battery",batteryVoltage.toString())
                            if (batteryVoltage != null) {
                                if(batteryVoltage in 4.0..5.0) {
                                    Icon(Icons.Filled.Battery4Bar, contentDescription = "batteryGood", tint = SuccessColor)
                                }else if(batteryVoltage > 5.0){
                                    Icon(Icons.Filled.BatteryFull, contentDescription = "batteryOk", tint = SuccessColor)
                                }else{
                                    Icon(painterResource(id = R.drawable.battery), contentDescription = "BatterLow", tint = ErrorColor)
                                }
                            }else{
                                Icon(Icons.Filled.Battery4Bar, contentDescription = "batteryGood", tint = SuccessColor)
                            }
                        }
                        Spacer(modifier = Modifier.width(1.dp))

                        if (batteryVoltage != null) {
                            if(batteryVoltage in 4.0..5.0) {
                                Text(text = stringResource(id = R.string.average), color = PrimaryColor, fontSize = 10.sp)
                            }else if(batteryVoltage > 5.0){
                                Text(text = stringResource(id = R.string.ok), color = PrimaryColor, fontSize = 10.sp)
                            }else{
                                Text(text = stringResource(id = R.string.low),color = PrimaryColor, fontSize = 10.sp)
                            }
                        }else{
                            Text(text = stringResource(id = R.string.average),color = PrimaryColor, fontSize = 10.sp)
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
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(modifier = Modifier
                        .clickable {
                            val batteryValue = batteryVoltage ?: -1
                            navController.navigate("feature/${deviceId}/controller")
                        }
                        .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                        .fillMaxWidth(0.8f),
                        color = PrimaryColor
                    ) {
                        Text(stringResource(id = R.string.start_driving),color = OnPrimary, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 12.dp), textAlign = TextAlign.Center)
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val items = features.value

                        val itemNames =  listOf("Home","Controller","Monitor","Debug")


                        itemsIndexed(items = itemNames) { _, item ->
                            if(item == "Home" || item == "Controller" || item == "Monitor" || item == "Debug"){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            when (item) {
                                                "Controller" -> {
                                                    val batteryValue = batteryVoltage ?: -1
                                                    navController.navigate("feature/${deviceId}/controller")
                                                }

                                                "Monitor" -> {
                                                    navController.navigate("feature/${deviceId}/plot")
                                                }

                                                "Debug" -> {
                                                    navController.navigate("feature/${deviceId}/debugConsole")
                                                }
                                                /*TODO: TO ADD FOTA ENABLE THIS */
//                                                "Fota" -> {
//                                                    navController.navigate("feature/${deviceId}/fota")
//                                                }
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

    if (showDialog.value) {
        Dialog(onDismissRequest = { showDialog.value = false }) {
            val dialogPainter = painterResource(id = R.drawable.bot_connected)

            Box(modifier = Modifier
                .width(337.dp)
                .height(449.dp)
                .padding(horizontal = 13.dp, vertical = 28.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(OnPrimary)
            ){
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)

                ) {
                    Text(
                        text = stringResource(id = R.string.dialog_header), style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 22.4.sp,
                            fontWeight = FontWeight(700),
                            color = PrimaryColor,
                            textAlign = TextAlign.Center,
                        )
                    )

                    Text(
                        text = stringResource(id = R.string.dialog_text),
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 19.6.sp,
                            fontWeight = FontWeight(400),
                            color = PrimaryColor,
                            textAlign = TextAlign.Center,
                        )
                    )

                    Image(painter = dialogPainter, contentDescription = "carImage",
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Surface(modifier = Modifier
                        .clickable { showDialog.value = false }
                        .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                        .fillMaxWidth(0.8f),
                        color = OnPrimary
                    ) {
                        Text(stringResource(id = R.string.dialog_option_homePage),color = Color(0xFF0047B2), fontSize = 15.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 12.dp), textAlign = TextAlign.Center)
                    }

                    Surface(modifier = Modifier
                        .clickable {
                            showDialog.value = false
                            val batteryValue = batteryVoltage ?: -1
                            navController.navigate("feature/${deviceId}/controller")
                        }
                        .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                        .fillMaxWidth(0.8f),
                        color = PrimaryColor
                    ) {
                        Text(stringResource(id = R.string.dialog_option_startDriving),color = OnPrimary, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 12.dp), textAlign = TextAlign.Center)
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
