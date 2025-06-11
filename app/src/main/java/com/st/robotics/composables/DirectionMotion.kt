package com.st.robotics.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.st.blue_sdk.features.extended.robotics_movement.RoboticsMovement
import com.st.blue_sdk.features.extended.robotics_movement.request.NavigationMode
import com.st.robotics.ui.theme.OnPrimary
import com.st.robotics.ui.theme.PrimaryColor
import com.st.robotics.ui.theme.TertiaryColor
import com.st.robotics.viewModels.ControllerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun DirectionMotion(
    onHandleMoved: ()->Unit,
    viewModel: ControllerViewModel,
    nodeId: String,
    isDisarmed: MutableState<NavigationMode>
) {
    var angle by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var lastSentAngle by remember { mutableIntStateOf(-1) }
    var shouldSendCommand by remember { mutableStateOf(false) }
    var lastCommandTimestamp = System.currentTimeMillis()
    val coroutineScope = rememberCoroutineScope()

    val firmwareVersion = viewModel.firmwareVersion.value
    //Eg. 123.456.789
    val versionPattern = Regex("""\d+\.\d+\.\d+""")

    val FIRMWARE_VERSION = "STM32H725IGT6_STSW-ROBKIT1_1.1.0"

    // Rotating Circle
    val ringRadius = 73.dp.dpToPx() - 10.dp.dpToPx()
    val angleRad = Math.toRadians(angle.toDouble())
    val circleX = (ringRadius * cos(angleRad - Math.PI / 2)).toFloat()
    val circleY = (ringRadius * sin(angleRad - Math.PI / 2)).toFloat()

    val parentBackgroundColor = TertiaryColor
    val knobColor = OnPrimary
    val hoverAreaColor = parentBackgroundColor.copy(alpha = 0.8f).let {
        Color(
            red = (it.red * 0.8f),
            green = (it.green * 0.8f),
            blue = (it.blue * 0.8f),
            alpha = 0.4f
        )
    }
    val gradientBrush = Brush.linearGradient(
        colors = listOf(PrimaryColor, Color.White),
        start = Offset(0f, 0f),
        end = Offset(900f, 900f)
    )

    val buttonColor = knobColor.copy(alpha = 0.6f).let {
        Color(
            red = (it.red * 0.8f),
            green = (it.green * 0.8f),
            blue = (it.blue * 0.8f),
            alpha = 0.4f
        )
    }


    if(versionPattern.find(firmwareVersion.toString())?.value == versionPattern.find(FIRMWARE_VERSION.toString())?.value){
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .alpha(if(isDisarmed.value == NavigationMode.DRIVE) 1f else if(isDisarmed.value == NavigationMode.LOCK) 0.6f else 0f)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val x = offset.x - (size.width / 2 + circleX)
                            val y = offset.y - (size.height / 2 + circleY)
                            isDragging =
                                hypot(x, y) <= 100.dp.toPx()
                        },
                        //WHEN USER LEAVE THE KNOB
                        onDragEnd = {
                            isDragging = false
                            angle = 0f

                            viewModel.sendCommand2(
                                featureName = RoboticsMovement.NAME,
                                deviceId = nodeId,
                                rotationAngle = 0
                            )
                        }
                    ) { change, _ ->
                        if (isDragging) {
                            val x = change.position.x - size.width / 2
                            val y = change.position.y - size.height / 2
                            val newAngle = ((Math.toDegrees(
                                atan2(
                                    y.toDouble(),
                                    x.toDouble()
                                )
                            ) + 450) % 360).toFloat()

                            if (newAngle != angle) {
                                angle = newAngle
                                shouldSendCommand = false
                                //1/2 SECOND DELAY FOR ANGLE CHANGE
//                                    delay(500)
                                shouldSendCommand = true
                                if (isDisarmed.value == NavigationMode.DRIVE) {
                                    val currentTime = System.currentTimeMillis()
                                    val timeDifference = currentTime - lastCommandTimestamp
                                    val angleInteger =  ((angle/10).roundToInt()*10.toDouble()).toInt()
                                    if (angleInteger != lastSentAngle && timeDifference >= 300) {
                                        if (angleInteger in 1..180) {
                                            viewModel.sendCommand2(
                                                featureName = RoboticsMovement.NAME,
                                                deviceId = nodeId,
                                                rotationAngle = angleInteger
                                            )
                                        } else if (angleInteger in 181..359) {
                                            viewModel.sendCommand2(
                                                featureName = RoboticsMovement.NAME,
                                                deviceId = nodeId,
                                                rotationAngle = angleInteger
                                            )
                                        }
                                        lastSentAngle = angleInteger
                                        lastCommandTimestamp = currentTime
                                    }
                                }

                            }
                        }

                        onHandleMoved()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Outer Ring with Marks
            Canvas(modifier = Modifier.size(150.dp)) {
                val ringWidth = 25.dp.toPx()
                val radius = size.width / 2 - ringWidth / 2
                drawCircle(
                    brush = gradientBrush,
                    style = Stroke(width = 4.dp.toPx()),
                    radius =( size.width + 2f)/2
                )
                drawCircle(
                    color = hoverAreaColor,
                    style = Stroke(width = ringWidth),
                    radius = radius
                )
                for (i in 0 until 360 step 1) {
                    val angleDeg = i * 10f - 90f
                    val radians = Math.toRadians(angleDeg.toDouble()).toFloat()

                    val markStart = Offset(
                        x = center.x + (radius - 15) * cos(radians),
                        y = center.y + (radius - 15) * sin(radians)
                    )

                    val markEnd = Offset(
                        x = center.x + (radius+15) * cos(radians),
                        y = center.y + (radius+15) * sin(radians)
                    )

                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.6f),
                        start = markStart,
                        end = markEnd,
                        strokeWidth = 3f
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(25.dp)
                    .offset {
                        IntOffset(circleX.toInt(), circleY.toInt())
                    }
                    .background(buttonColor, CircleShape)
            )

            // Degree Text - hidden for now
//            Text(
//                text = "${((angle/10).roundToInt()*10.toDouble()).toInt()}°",
//                fontWeight = FontWeight.Normal,
//                fontSize = 20.sp,
//                color = Color.Transparent,
//                modifier = Modifier.align(Alignment.Center)
//            )
        }
    }else{
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .alpha(if(isDisarmed.value == NavigationMode.DRIVE) 1f else if(isDisarmed.value == NavigationMode.LOCK) 0.6f else 0f)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val x = offset.x - (size.width / 2 + circleX)
                            val y = offset.y - (size.height / 2 + circleY)
                            isDragging =
                                hypot(x, y) <= 100.dp.toPx()
                        },
                        //WHEN USER LEAVE THE KNOB
                        onDragEnd = {
                            isDragging = false
                            angle = 0f
                        }
                    ) { change, _ ->
                        if (isDragging) {
                            val x = change.position.x - size.width / 2
                            val y = change.position.y - size.height / 2
                            val newAngle = ((Math.toDegrees(
                                atan2(
                                    y.toDouble(),
                                    x.toDouble()
                                )
                            ) + 450) % 360).toFloat()

                            if (newAngle != angle) {
                                angle = newAngle
                                shouldSendCommand = false
                                coroutineScope.launch {
                                    //1/2 SECOND DELAY FOR ANGLE CHANGE
                                    delay(500)
                                    shouldSendCommand = true
                                    if (isDisarmed.value == NavigationMode.DRIVE && shouldSendCommand) {
                                        val angleInteger =  ((angle/10).roundToInt()*10.toDouble()).toInt()
                                        if (angleInteger != lastSentAngle) {
                                            if (angleInteger in 1..180) {
                                                viewModel.sendCommand(
                                                    featureName = RoboticsMovement.NAME,
                                                    deviceId = nodeId,
                                                    action = ControllerAction.Right,
                                                    angle = angleInteger
                                                )
                                            } else if (angleInteger in 181..359) {
                                                viewModel.sendCommand(
                                                    featureName = RoboticsMovement.NAME,
                                                    deviceId = nodeId,
                                                    action = ControllerAction.Left,
                                                    angle = angleInteger
                                                )
                                            }
                                            lastSentAngle = angleInteger
                                        }
                                    }
                                }
                            }
                        }

                        onHandleMoved()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Outer Ring with Marks
            Canvas(modifier = Modifier.size(150.dp)) {
                val ringWidth = 25.dp.toPx()
                val radius = size.width / 2 - ringWidth / 2
                drawCircle(
                    brush = gradientBrush,
                    style = Stroke(width = 4.dp.toPx()),
                    radius =( size.width + 2f)/2
                )
                drawCircle(
                    color = hoverAreaColor,
                    style = Stroke(width = ringWidth),
                    radius = radius
                )
                for (i in 0 until 360 step 1) {
                    val angleDeg = i * 10f - 90f
                    val radians = Math.toRadians(angleDeg.toDouble()).toFloat()

                    val markStart = Offset(
                        x = center.x + (radius - 15) * cos(radians),
                        y = center.y + (radius - 15) * sin(radians)
                    )

                    val markEnd = Offset(
                        x = center.x + (radius+15) * cos(radians),
                        y = center.y + (radius+15) * sin(radians)
                    )

                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.6f),
                        start = markStart,
                        end = markEnd,
                        strokeWidth = 3f
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(25.dp)
                    .offset {
                        IntOffset(circleX.toInt(), circleY.toInt())
                    }
                    .background(buttonColor, CircleShape)
            )

            // Degree Text
            Text(
                text = "${((angle/10).roundToInt()*10.toDouble()).toInt()}°",
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = Color.LightGray,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }


}

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }