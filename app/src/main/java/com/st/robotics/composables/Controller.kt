package com.st.robotics.composables

import android.app.Activity
import android.graphics.Paint
import android.os.Build
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.ActionBar
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Battery1Bar
import androidx.compose.material.icons.filled.Battery4Bar
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.st.robotics.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import com.st.blue_sdk.features.extended.ext_configuration.ExtConfiguration
import com.st.blue_sdk.features.extended.robotics_movement.request.NavigationMode
import com.st.robotics.models.DeveloperMode
import com.st.robotics.ui.theme.ErrorColor
import com.st.robotics.ui.theme.OnPrimary
import com.st.robotics.ui.theme.PrimaryColor
import com.st.robotics.ui.theme.SecondaryColor
import com.st.robotics.ui.theme.SuccessColor
import com.st.robotics.utilities.ChangeOrientationToLandscape
import com.st.robotics.viewModels.BleDeviceDetailViewModel
import com.st.robotics.viewModels.ControllerViewModel
import com.st.blue_sdk.models.NodeState
import com.st.robotics.models.FirmwareDetails
import com.st.robotics.views.PlotChartV2
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


@Composable
fun Controller(viewModel: ControllerViewModel,nodeId : String,navController: NavController){

    val isDisarmed = remember { mutableStateOf(NavigationMode.LOCK) }
    val shake = remember { Animatable(0f) }
    var trigger by remember { mutableLongStateOf(0L) }
    val view = LocalView.current
    val context = LocalContext.current

    val darkBackgroundColor = Color(0xFF03234B)
    val lightBackgroundColor = Color(0xFF0A357E)

    var selectedIndex by remember { mutableIntStateOf(0) }

    val options = if(DeveloperMode.isDeveloper == true){
        listOf(NavigationMode.LOCK,NavigationMode.DRIVE,NavigationMode.FOLLOW_ME,NavigationMode.AUTOPILOT)
    }else{
        listOf(NavigationMode.LOCK,NavigationMode.DRIVE,NavigationMode.AUTOPILOT)
    }

    val firmwareVersion = viewModel.firmwareVersion.value

    LaunchedEffect(trigger) {
        if (trigger != 0L) {
            for (i in 0..10) {
                when (i % 2) {
                    0 -> shake.animateTo(5f, spring(stiffness = 100_000f))
                    else -> shake.animateTo(-5f, spring(stiffness = 100_000f))
                }
            }
            shake.animateTo(0f)
        }
    }

    LaunchedEffect(Unit) {
        ChangeOrientationToLandscape(context = context)
        viewModel.getFwVersion(nodeId = nodeId, featureName = ExtConfiguration.NAME)
    }

    val gradientBrush = Brush.radialGradient(
        0.0f to darkBackgroundColor,
        0.2f to darkBackgroundColor,
        1f to lightBackgroundColor ,
        radius = 1400.0f,
    )

    val borderBrush = Brush.linearGradient(
        colors = listOf(PrimaryColor, Color.White),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(200f, 200f)
    )

    val bleDevice = viewModel.bleDevice(deviceId = nodeId).collectAsState(initial = null)

    val rssiData : String = bleDevice.value?.rssi?.rssi.toString()
    val batteryData by viewModel.batteryData.collectAsState(initial = null)
    val batteryPercentage = batteryData?.percentage?.value?.toInt()
    val batteryVoltage = batteryData?.voltage?.value?.toFloat()

    val backHandlingEnabled by remember { mutableStateOf(true) }

    var isFeaturesFetched by remember { mutableStateOf(false) }

    if(bleDevice.value?.connectionStatus?.current == NodeState.Ready && !isFeaturesFetched){
        viewModel.getRssi(deviceId = nodeId)
        isFeaturesFetched = true
    }

    BackHandler(enabled = backHandlingEnabled) {
        viewModel.sendNavigationCommand(
            NavigationMode.LOCK,
            deviceId = nodeId
        )
        navController.popBackStack()
    }


    //TO HANDLE THE BEHAVIOUR OF SYSTEM INSETS
    DisposableEffect(context) {
        val window = (context as Activity).window
        val controller = WindowCompat.getInsetsController(window, view)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }else{
            // Hide system bars and make the content appear behind them
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//            controller.isAppearanceLightStatusBars = false
//            controller.isAppearanceLightNavigationBars = false
            controller.hide(WindowInsetsCompat.Type.systemBars())
        }

        onDispose {
            viewModel.disableFeatures(deviceId = nodeId)
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }


    Row(modifier = Modifier
        .fillMaxSize()
        .background(brush = gradientBrush)
    ) {

        //left column
        Column(modifier = Modifier
            .fillMaxWidth( if (options[selectedIndex] != NavigationMode.AUTOPILOT){
                1f/3f
            } else {
                0.2f
            }
            )
        ) {
            //Top Left close button
            Row(modifier = Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth()
                .padding(20.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = {
                    viewModel.sendNavigationCommand(NavigationMode.LOCK,deviceId = nodeId)
                    navController.popBackStack() }
                    ,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray.copy(alpha = 0.4f))
                        .border(
                            width = 1.dp,
                            brush = borderBrush,
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Close Button",
                        tint = OnPrimary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = 2.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if(isDisarmed.value == NavigationMode.LOCK|| isDisarmed.value == NavigationMode.DRIVE)
                    {
                        Text(color = OnPrimary, fontSize = 15.sp, text = stringResource(id = R.string.throttle))
                    }

                    JoyStick( onHandleMoved = {
                        trigger = if(isDisarmed.value == NavigationMode.LOCK){
                            System.currentTimeMillis()
                        }else{
                            0
                        }
                    },
                        viewModel = hiltViewModel(),
                        nodeId = nodeId,
                        isDisarmed
                    )
                }

                if(firmwareVersion != null){
                    Text(color = OnPrimary, fontSize = 10.sp, text = "${firmwareVersion}", textAlign = TextAlign.Start,modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, bottom = 8.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }else{
                    Text(color = OnPrimary, fontSize = 10.sp, text = "",textAlign = TextAlign.Start,modifier = Modifier.align(Alignment.Start).padding(start = 8.dp))
                }

            }
        }

        //Mid Column
        Column(modifier = Modifier
            .fillMaxWidth(if(options[selectedIndex] != NavigationMode.AUTOPILOT){
                    0.5f
                } else {
                    0.75f
                }
            )
            .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //Custom Slider
            val customPadding = if(DeveloperMode.isDeveloper == true){
                5
            }else{
                10
            }
            Box(modifier = Modifier.
            fillMaxHeight(
                if(options[selectedIndex] == NavigationMode.AUTOPILOT){
                    0.6f
                } else {
                    0f
                }
            )
            ) {
                if(options[selectedIndex] == NavigationMode.AUTOPILOT)
                {
                    ControllerSensorData(
                        viewModel = hiltViewModel(),
                        navController = navController,
                        deviceId = nodeId
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(if (options[selectedIndex] != NavigationMode.AUTOPILOT){
                        1f
                    } else {
                        0.528f
                    })
                    .offset { IntOffset(shake.value.roundToInt(), y = 0) },
            ) {
                Spacer(modifier = Modifier.height(1.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    options.forEachIndexed { index, option ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            androidx.compose.material3.Text(
                                text = option.name,
                                fontSize = 12.sp,
                                fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedIndex == index) OnPrimary else Color(0xFF7A7A7A),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(if (selectedIndex == index) 20.dp else 15.dp)
                                    .background(
                                        color = if (selectedIndex == index) OnPrimary else Color(
                                            0xFF7A7A7A
                                        )
                                    )
                            )
                        }
                    }
                }
//                Spacer(modifier = Modifier.height(8.dp))
                BoxWithConstraints(
                    modifier = Modifier
                        .border(
                            1.dp,
                            color = Color(0xFFDBDEE2),
                            shape = RoundedCornerShape(size = 8.dp)
                        )
                        .height(60.dp)
                        .background(
                            color = Color(0xFF01142C),
                            shape = RoundedCornerShape(size = 6.dp)
                        )
                ) {
                    val boxWidthPx = constraints.maxWidth.toFloat()
                    val density = LocalDensity.current
                    val boxWidthDp = with(density) { boxWidthPx.toDp() }
                    val segmentWidth = boxWidthDp / options.size

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        options.forEachIndexed { index, _ ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable {
                                        selectedIndex = index
                                        isDisarmed.value = options[selectedIndex]

                                        if (options[selectedIndex] == NavigationMode.FOLLOW_ME) {
                                            viewModel.sendNavigationCommand(
                                                NavigationMode.FOLLOW_ME,
                                                deviceId = nodeId
                                            )
                                        } else if (options[selectedIndex] == NavigationMode.AUTOPILOT) {
                                            viewModel.sendNavigationCommand(
                                                NavigationMode.AUTOPILOT,
                                                deviceId = nodeId
                                            )
                                        } else if (options[selectedIndex] == NavigationMode.DRIVE) {
                                            viewModel.sendNavigationCommand(
                                                NavigationMode.DRIVE,
                                                deviceId = nodeId
                                            )
                                        } else if(options[selectedIndex] == NavigationMode.LOCK){
                                            viewModel.sendNavigationCommand(
                                                NavigationMode.LOCK,
                                                deviceId = nodeId
                                            )
                                        }
                                    },
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.8f)
                            .width(segmentWidth/2.5f)
                            .offset(x = (selectedIndex * segmentWidth) + ((segmentWidth - (segmentWidth / 2.5f)) / 2),y=6.dp)
                            .background(Color(0xFF0A357E), shape = RoundedCornerShape(8.dp))
                            ,
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.25f)
                                .fillMaxHeight(0.5f)
                                .background(Color(0xFF85CAE7), shape = RoundedCornerShape(20.dp))
                        )
                    }
                }
            }
        }

        //Right Column
        Column(modifier = Modifier
            .fillMaxWidth()) {
            //Top right status icon row
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .padding(vertical = 20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(10.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Row (
                        modifier = Modifier.fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        if (batteryVoltage != null) {
                            if(batteryVoltage in 4.0..5.0) {
                                Icon(Icons.Filled.Battery4Bar, contentDescription = "batteryGood", tint = OnPrimary)
                            }else if(batteryVoltage > 5.0){
                                Icon(Icons.Filled.BatteryFull, contentDescription = "batteryOk", tint = OnPrimary)
                            }else{
                                Icon(painterResource(id = R.drawable.battery), contentDescription = "BatterLow", tint = OnPrimary)
                            }
                        }else{
                            Icon(Icons.Filled.Battery4Bar, contentDescription = "batteryGood", tint = OnPrimary)
                        }
                        Spacer(modifier = Modifier.width(2.dp))

                        if (batteryVoltage != null) {
                            if(batteryVoltage in 4.0..5.0) {
                                androidx.compose.material3.Text(
                                    text = stringResource(id = R.string.average),
                                    color = OnPrimary,
                                    fontSize = 10.sp
                                )
                            }else if(batteryVoltage > 5.0){
                                androidx.compose.material3.Text(
                                    text = stringResource(id = R.string.ok),
                                    color = OnPrimary,
                                    fontSize = 10.sp
                                )
                            }else{
                                androidx.compose.material3.Text(
                                    text = stringResource(id = R.string.low),
                                    color = OnPrimary,
                                    fontSize = 10.sp
                                )
                            }
                        }else{
                            androidx.compose.material3.Text(
                                text = stringResource(id = R.string.average),
                                color = OnPrimary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Row (
                        modifier = Modifier.fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(
                            imageVector = Icons.Default.SignalCellularAlt,
                            contentDescription = "Close Button",
                            tint = Color.White
                        )
                        Text(text = "$rssiData dBm", fontSize = 10.sp, color = Color.White)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(isDisarmed.value == NavigationMode.LOCK || isDisarmed.value == NavigationMode.DRIVE)
                {
                    Text(color = OnPrimary, fontSize = 15.sp, text = stringResource(id = R.string.direction))
                }
                Spacer(modifier = Modifier.height(10.dp))
                DirectionMotion(
                    onHandleMoved = {
                        trigger = if (isDisarmed.value == NavigationMode.LOCK) {
                            System.currentTimeMillis()
                        } else {
                            0
                        }
                    },
                    viewModel = hiltViewModel(),
                    nodeId = nodeId,
                    isDisarmed = isDisarmed
                )
            }
        }
    }
}

enum class ControllerAction{
    Forward , Backward , Right , Left , Stop
}

@Composable
fun ArmDisarmText(isDisarmed: State<Boolean>) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isDisarmed.value) {
        if (isDisarmed.value) {
            isVisible = true
            delay(3000) // Delay for 3 seconds
            isVisible = false
        }
    }

    if(isDisarmed.value && isVisible){
        Text(text = "Active", color = OnPrimary)
    }else if(isDisarmed.value && !isVisible){
        Text(text = "", color = OnPrimary)
    }else{
        Text(text = "Inactive", color = OnPrimary)
    }
}

