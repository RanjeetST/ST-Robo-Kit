package com.st.robotics.composables

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


@Composable
fun Controller(viewModel: ControllerViewModel,nodeId : String,navController: NavController,batteryVoltage : Int){
    ChangeOrientationToLandscape(context = LocalContext.current)
    val isDisarmed = remember { mutableStateOf("Lock") }
    val shake = remember { Animatable(0f) }
    var trigger by remember { mutableLongStateOf(0L) }

    val darkBackgroundColor = Color(0xFF03234B)
    val lightBackgroundColor = Color(0xFF0A357E)

    var selectedIndex by remember { mutableIntStateOf(0) }

    val options = if(DeveloperMode.isDeveloper == true){
         listOf("Lock","Drive","Follow","Autopilot")
    }else{
        listOf("Lock", "Drive", "Autopilot")
    }

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
    val view = LocalView.current
    val context = LocalContext.current

    val bleDevice = viewModel.bleDevice(deviceId = nodeId).collectAsState(initial = null)

    val rssiData : String = bleDevice.value?.rssi?.rssi.toString()

    val backHandlingEnabled by remember { mutableStateOf(true) }

    var isFeaturesFetched by remember { mutableStateOf(false) }

    if(bleDevice.value?.connectionStatus?.current == NodeState.Ready && !isFeaturesFetched){
        viewModel.getRssi(deviceId = nodeId)
        isFeaturesFetched = true
    }

    BackHandler(enabled = backHandlingEnabled) {
        navController.popBackStack()
    }


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
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
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
            .fillMaxWidth(0.3f)
        ) {
            //Top Left close button
            Row(modifier = Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth()
                .padding(20.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = {navController.popBackStack() },
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
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(isDisarmed.value == "Lock" || isDisarmed.value == "Drive")
                {
                    Text(color = OnPrimary, fontSize = 15.sp, text = stringResource(id = R.string.throttle))
                }


                JoyStick( onHandleMoved = {
                    trigger = if(isDisarmed.value == "Lock"){
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
        }

        //Mid Column
        Column(modifier = Modifier
            .fillMaxWidth(0.5f)) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .padding(vertical = 20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally){
                        if(batteryVoltage in 4..5) {
                            Icon(Icons.Filled.Battery4Bar, contentDescription = "batteryGood", tint = SuccessColor)
                        }else if(batteryVoltage == -1){
                            Icon(Icons.Filled.BatteryFull, contentDescription = "batteryGood", tint = SecondaryColor)
                        }else if(batteryVoltage > 5){
                            Icon(Icons.Filled.BatteryFull, contentDescription = "BatterLow", tint = SuccessColor)
                        }else if(batteryVoltage < 4){
                            Icon(Icons.Filled.Battery1Bar, contentDescription = "BatterLow", tint = ErrorColor)
                        }

                        if(batteryVoltage in 4..5) {
                            androidx.compose.material3.Text(text = stringResource(id = R.string.average), fontSize = 10.sp, color = OnPrimary)
                        }else if(batteryVoltage > 5){
                            androidx.compose.material3.Text(text = stringResource(id = R.string.ok), fontSize = 10.sp, color = OnPrimary)
                        }else if(batteryVoltage == -1){
                            androidx.compose.material3.Text(text = stringResource(id = R.string.na), fontSize = 10.sp, color = OnPrimary)
                        }else{
                            androidx.compose.material3.Text(text = stringResource(id = R.string.low), fontSize = 10.sp, color = OnPrimary)
                        }
                    }


                    Spacer(modifier = Modifier.width(10.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally){
                        Icon(
                            imageVector = Icons.Default.SignalCellularAlt,
                            contentDescription = "Close Button",
                            tint = SecondaryColor
                        )
                        Text(text = "$rssiData dBm", fontSize = 10.sp, color = OnPrimary)
                    }
                }
            }

            //Custom Slider
            val customPadding = if(DeveloperMode.isDeveloper == true){
                5
            }else{
                10
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .offset { IntOffset(shake.value.roundToInt(), y = 0) }
            ) {
                Spacer(modifier = Modifier.height(8.dp))
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
                                text = option,
                                fontSize = 12.sp,
                                fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedIndex == index) OnPrimary else Color(0xFF7A7A7A),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(if (selectedIndex == index) 25.dp else 20.dp)
                                    .background(
                                        color = if (selectedIndex == index) OnPrimary else Color(
                                            0xFF7A7A7A
                                        )
                                    )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                BoxWithConstraints(
                    modifier = Modifier
                        .border(
                            1.dp,
                            color = Color(0xFFDBDEE2),
                            shape = RoundedCornerShape(size = 6.dp)
                        )
                        .height(70.dp)
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

                                        if (options[selectedIndex] == "Follow") {
                                            viewModel.sendNavigationCommand(
                                                BleDeviceDetailViewModel.Commands.FOLLOW_ME,
                                                deviceId = nodeId
                                            )
                                        } else if (options[selectedIndex] == "Autopilot") {
                                            viewModel.sendNavigationCommand(
                                                BleDeviceDetailViewModel.Commands.FREE_NAVIGATION,
                                                deviceId = nodeId
                                            )
                                        } else if (options[selectedIndex] == "Drive") {
                                            viewModel.sendNavigationCommand(
                                                BleDeviceDetailViewModel.Commands.REMOTE_CONTROL,
                                                deviceId = nodeId
                                            )
                                        }
                                    },
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxHeight()
                            .width(30.dp)
                            .offset(x = (selectedIndex * segmentWidth) + customPadding.dp)
                            .background(Color(0xFF0A357E), shape = RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.4f)
                                .fillMaxHeight(0.7f)
                                .background(Color(0xFF85CAE7), shape = RoundedCornerShape(25.dp))
                        )
                    }
                }
            }
        }

        //Right Column
        Column(modifier = Modifier
            .fillMaxWidth()) {
            //Top right status icon row
            Row(modifier = Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                ,
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.End
            ) {
//                ***********************FUTURE USE*****************************
//                IconButton(onClick = { /* Handle close action */ },
//                    modifier = Modifier
//                        .size(40.dp)
//                        .clip(RoundedCornerShape(10.dp))
//                        .background(Color.LightGray.copy(alpha = 0.4f))
//                        .border(
//                            width = 1.dp,
//                            brush = borderBrush,
//                            shape = RoundedCornerShape(10.dp)
//                        )
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Settings,
//                        contentDescription = "Settings",
//                        tint = OnPrimary
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(16.dp))
//
//                IconButton(onClick = { /* Handle close action */ },
//                    modifier = Modifier
//                        .size(40.dp)
//                        .clip(RoundedCornerShape(10.dp))
//                        .background(Color.LightGray.copy(alpha = 0.4f))
//                        .border(
//                            width = 1.dp,
//                            brush = borderBrush,
//                            shape = RoundedCornerShape(10.dp)
//                        )
//
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Help,
//                        contentDescription = "Help",
//                        tint = OnPrimary
//                    )
//                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(isDisarmed.value == "Lock" || isDisarmed.value == "Drive")
                {
                    Text(color = OnPrimary, fontSize = 15.sp, text = stringResource(id = R.string.direction))
                }
                Spacer(modifier = Modifier.height(10.dp))
                DirectionMotion(
                    onHandleMoved = {
                        trigger = if (isDisarmed.value == "Lock") {
                            System.currentTimeMillis()
                        } else {
                            0
                        }
                    },
                    viewModel = hiltViewModel(),
                    nodeId = nodeId,
                    isDisarmed = isDisarmed)
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

