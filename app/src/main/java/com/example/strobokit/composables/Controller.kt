package com.example.strobokit.composables

import android.app.Activity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.ui.theme.TertiaryColor
import com.example.strobokit.utilities.ChangeOrientationToLandscape
import com.example.strobokit.viewModels.ControllerViewModel
import kotlin.math.roundToInt


@Composable
fun Controller(viewModel: ControllerViewModel,nodeId : String,navController: NavController){
    val isDisarmed = remember { mutableStateOf(false) }
    val shake = remember { Animatable(0f) }
    var trigger by remember { mutableStateOf(0L) }
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
        0.0f to TertiaryColor,
        1f to PrimaryColor,
        radius = 1400.0f,
        tileMode = TileMode.Repeated
    )
    val borderBrush = Brush.linearGradient(
        colors = listOf(PrimaryColor, Color.White),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(200f, 200f)
    )
    val view = LocalView.current
    val context = LocalContext.current

    DisposableEffect(context) {
        val window = (context as Activity).window
        val controller = WindowCompat.getInsetsController(window, view)

        // Hide system bars and make the content appear behind them
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
        controller.hide(WindowInsetsCompat.Type.systemBars())

        onDispose {
            // Show system bars when leaving this screen
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    ChangeOrientationToLandscape(context = LocalContext.current)

    Row(modifier = Modifier
        .fillMaxSize()
        .background(brush = gradientBrush)) {

        //left column
        Column(modifier = Modifier
            .fillMaxWidth(0.3f)
        ) {
            //Top Left close button
            Row(modifier = Modifier
                .fillMaxHeight(0.4f)
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(color = OnPrimary, fontSize = 18.sp, text = "Linear Motion")

                JoyStick( onHandleMoved = {
                    trigger = if(!isDisarmed.value){
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
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally){

                Box(
                    modifier = Modifier
                        .offset { IntOffset(shake.value.roundToInt(), y = 0) }
                ) {
                    Switch(
                        checked = isDisarmed.value,
                        onCheckedChange = { isDisarmed.value = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFF611616),
                            checkedTrackColor = Color(0xFF11FF00)
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
                if(isDisarmed.value)
                {
                    Text(text = "Armed", color = OnPrimary)
                }else{
                    Text(text = "Disarmed", color = OnPrimary)
                }

            }
        }

        //Right Column
        Column(modifier = Modifier
            .fillMaxWidth()) {
            //Top right status icon row
            Row(modifier = Modifier
                .fillMaxHeight(0.4f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                ,
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = { /* Handle close action */ },
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
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Close Button",
                        tint = OnPrimary
                    )
                }
                IconButton(onClick = { /* Handle close action */ },
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
                        imageVector = Icons.Default.BatteryFull,
                        contentDescription = "Close Button",
                        tint = OnPrimary
                    )
                }
                IconButton(onClick = { /* Handle close action */ },
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
                        imageVector = Icons.Default.SignalCellularAlt,
                        contentDescription = "Close Button",
                        tint = OnPrimary
                    )
                }
                IconButton(onClick = { /* Handle close action */ },
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
                        imageVector = Icons.Default.Help,
                        contentDescription = "Close Button",
                        tint = OnPrimary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(color = OnPrimary, fontSize = 18.sp, text = "Direction Motion")
                Spacer(modifier = Modifier.height(10.dp))
                DirectionMotion(
                    onHandleMoved = {
                        trigger = if (!isDisarmed.value) {
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

enum class controllerAction{
    Forward , Backward , Right , Left , Stop
}


@Composable
@Preview( widthDp = 800, heightDp = 400, apiLevel = 34)
fun ControllerPreview(){
    val isDisarmed = remember { mutableStateOf(false) }
    val shake = remember { Animatable(0f) }
    var trigger by remember { mutableStateOf(0L) }
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
        0.0f to TertiaryColor,
        1f to PrimaryColor,
        radius = 1400.0f,
        tileMode = TileMode.Repeated
    )

    val borderBrush = Brush.linearGradient(
        colors = listOf(PrimaryColor, Color.White),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(200f, 200f)
    )

    Row(modifier = Modifier
        .fillMaxSize()
        .background(brush = gradientBrush)) {

        //left column
        Column(modifier = Modifier
            .fillMaxWidth(0.3f)
        ) {
            //Top Left close button
            Row(modifier = Modifier
                .fillMaxHeight(0.4f)
                .fillMaxWidth()
                .padding(20.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = { /* Handle close action */ },
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(color = OnPrimary, fontSize = 18.sp, text = "Linear Motion")

                    JoyStick(
                        onHandleMoved = {
                            trigger = if (!isDisarmed.value) {
                                System.currentTimeMillis()
                            } else {
                                0
                            }
                        },
                        viewModel = hiltViewModel(),
                        nodeId = "",
                        isDisarmed
                    )
            }
        }

        //Mid Column
        Column(modifier = Modifier
            .fillMaxWidth(0.5f)) {

            Column(modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally){


                Box(
                    modifier = Modifier
                        .offset { IntOffset(shake.value.roundToInt(), y = 0) }
                ) {
                    Switch(
                        checked = isDisarmed.value,
                        onCheckedChange = { isDisarmed.value = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFF611616),
                            checkedTrackColor = Color(0xFF11FF00)
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Text(text = "Disarmed", color = OnPrimary)
            }
        }

        //Right Column
        Column(modifier = Modifier
            .fillMaxWidth()) {
            //Top right status icon row
            Row(modifier = Modifier
                .fillMaxHeight(0.4f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                ,
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = { /* Handle close action */ },
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
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Close Button",
                        tint = OnPrimary
                    )
                }
                IconButton(onClick = { /* Handle close action */ },
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
                        imageVector = Icons.Default.BatteryFull,
                        contentDescription = "Close Button",
                        tint = OnPrimary
                    )
                }
                IconButton(onClick = { /* Handle close action */ },
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
                        imageVector = Icons.Default.SignalCellularAlt,
                        contentDescription = "Close Button",
                        tint = OnPrimary
                    )
                }
                IconButton(onClick = { /* Handle close action */ },
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
                        imageVector = Icons.Default.Help,
                        contentDescription = "Close Button",
                        tint = OnPrimary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(color = OnPrimary, fontSize = 18.sp, text = "Direction Motion")
                Spacer(modifier = Modifier.height(10.dp))
                    DirectionMotion(
                     onHandleMoved = {
                        trigger = if (!isDisarmed.value) {
                            System.currentTimeMillis()
                        } else {
                            0
                        }
                    },
                        viewModel = hiltViewModel(),
                        nodeId = "",
                        isDisarmed)
            }
        }
    }
}

