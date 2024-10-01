package com.example.strobokit.composables

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.ui.theme.TertiaryColor
import com.example.strobokit.viewModels.ControllerViewModel
import com.st.blue_sdk.features.switchfeature.SwitchStatusType


@Composable
fun JoyStick(onHandleMoved: ()-> Unit,viewModel: ControllerViewModel,nodeId : String,isDisarmed : MutableState<Boolean>) {
    val parentBackgroundColor = TertiaryColor
    val gradientBrush = Brush.linearGradient(
        colors = listOf(PrimaryColor, Color.White),
        start = Offset(0f, 0f),
        end = Offset(900f, 900f)
    )
    val buttonColor = PrimaryColor.copy(alpha = 0.8f).let {
        Color(
            red = (it.red * 0.8f).toFloat(),
            green = (it.green * 0.8f).toFloat(),
            blue = (it.blue * 0.8f).toFloat(),
            alpha = 0.3f // Adjust transparency here
        )
    }
    var joystickCenter by remember { mutableStateOf(Offset.Zero) }
    var handlePosition by remember { mutableStateOf(Offset.Zero) }
    var lastCommand by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .padding(6.dp)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(150.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if(isDisarmed.value == true)
                            {
                                Log.d("JOYSTICK", "Stop Called")
                                viewModel.sendCommand(
                                    featureName = "Switch",
                                    nodeId,
                                    SwitchStatusType.On,
                                    controllerAction.Stop
                                )
                            }

                           handlePosition = Offset.Zero
                            lastCommand = ""
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        handlePosition = Offset(
                            x = 0f, // Restrict movement to the Y-axis
                            y = (handlePosition.y + dragAmount.y).coerceIn(-120f, 120f)
                        )

                        if(isDisarmed.value == true){
                            when (handlePosition.y.toInt()) {
                                -120 -> {
                                    if (lastCommand != "Forward") {
                                        Log.d("JOYSTICK", "Forward Called")
                                        viewModel.sendCommand(
                                            featureName = "Switch",
                                            nodeId,
                                            SwitchStatusType.On,
                                            controllerAction.Forward
                                        )
                                        lastCommand = "Forward"
                                    }
                                }
                                120 -> {
                                    if (lastCommand != "Backward") {
                                        Log.d("JOYSTICK", "Backward Called")
                                        viewModel.sendCommand(
                                            featureName = "Switch",
                                            nodeId,
                                            SwitchStatusType.On,
                                            controllerAction.Backward
                                        )
                                        lastCommand = "Backward"
                                    }
                                }
                                else -> {
                                    lastCommand = ""
                                }
                            }
                        }

                        onHandleMoved()
                    }
                }
        ) {
            joystickCenter = center

            // Draw the outer circle
            drawCircle(
                brush = gradientBrush,
                radius = size.minDimension / 2,
                style = Stroke(width = 2.dp.toPx())
            )

            // Draw the handle
            drawCircle(
                color = buttonColor,
                radius = size.minDimension / 6,
                center = joystickCenter + handlePosition
            )
        }

        // Overlay the arrow buttons
        Box(
            modifier = Modifier
                .size(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {

                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up")


                Spacer(modifier = Modifier.weight(1f))


                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down")

            }
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 800, heightDp = 400)
fun JoyStickPreview() {
    val parentBackgroundColor = TertiaryColor
    val gradientBrush = Brush.linearGradient(
        colors = listOf(PrimaryColor, Color.White),
        start = Offset(0f, 0f),
        end = Offset(100f, 100f)
    )
    val buttonColor = PrimaryColor.copy(alpha = 0.8f).let {
        Color(
            red = (it.red * 0.8f).toFloat(),
            green = (it.green * 0.8f).toFloat(),
            blue = (it.blue * 0.8f).toFloat(),
            alpha = 0.3f // Adjust transparency here
        )
    }
    var joystickCenter by remember { mutableStateOf(Offset.Zero) }
    var handlePosition by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier = Modifier
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(150.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            handlePosition = Offset.Zero
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        handlePosition = Offset(
                            x = 0f, // Restrict movement to the Y-axis
                            y = (handlePosition.y + dragAmount.y).coerceIn(-120f, 120f)
                        )
                    }
                }
        ) {
            joystickCenter = center

            // Draw the outer circle
            drawCircle(
                brush = gradientBrush,
                radius = size.minDimension / 2,
                style = Stroke(width = 2.dp.toPx())
            )

            // Draw the handle
            drawCircle(
                color = buttonColor,
                radius = size.minDimension / 6,
                center = joystickCenter + handlePosition
            )
        }

        // Overlay the arrow buttons
        Box(
            modifier = Modifier
                .size(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {

                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up")


                Spacer(modifier = Modifier.weight(1f))


                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down")

            }
        }
    }
}