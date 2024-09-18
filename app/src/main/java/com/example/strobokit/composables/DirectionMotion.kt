package com.example.strobokit.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.ui.theme.TertiaryColor
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun DirectionMotion(onHandleMoved : ()->Unit) {
    var angle by remember { mutableStateOf(0f) } // Start from the top
    var isDragging by remember { mutableStateOf(false) }
    // Rotating Circle
    val ringRadius = 73.dp.dpToPx() - 10.dp.dpToPx() // Adjust radius to fit within the ring
    val angleRad = Math.toRadians(angle.toDouble())
    val circleX = (ringRadius * cos(angleRad - Math.PI / 2)).toFloat() // Adjust for 0 degrees at the top
    val circleY = (ringRadius * sin(angleRad - Math.PI / 2)).toFloat() // Adjust for 0 degrees at the top

    val parentBackgroundColor = TertiaryColor
    val knobColor = PrimaryColor
    val hoverAreaColor = parentBackgroundColor.copy(alpha = 0.8f).let {
        Color(
            red = (it.red * 0.8f).toFloat(),
            green = (it.green * 0.8f).toFloat(),
            blue = (it.blue * 0.8f).toFloat(),
            alpha = 0.4f // Adjust transparency here
        )
    }
    val gradientBrush = Brush.linearGradient(
        colors = listOf(PrimaryColor, Color.White),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(900f, 900f)
    )
    val buttonColor = knobColor.copy(alpha = 0.8f).let {
        Color(
            red = (it.red * 0.8f).toFloat(),
            green = (it.green * 0.8f).toFloat(),
            blue = (it.blue * 0.8f).toFloat(),
            alpha = 0.4f // Adjust transparency here
        )
    }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val x = offset.x - (size.width / 2 + circleX)
                        val y = offset.y - (size.height / 2 + circleY)
                        isDragging =
                            hypot(x, y) <= 100.dp.toPx() // Check if touch is within the circle
                    },
                    onDragEnd = {
                        isDragging = false
                        angle = 0f // Reset angle to 0 degrees when drag ends
                    }
                ) { change, _ ->
                    if (isDragging) {
                        val x = change.position.x - size.width / 2
                        val y = change.position.y - size.height / 2
                        angle = ((Math.toDegrees(
                            atan2(
                                y.toDouble(),
                                x.toDouble()
                            )
                        ) + 450) % 360).toFloat() // Adjust for 0 degrees at the top
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

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }