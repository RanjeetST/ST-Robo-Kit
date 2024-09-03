package com.example.strobokit.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.strobokit.ui.theme.TertiaryColor


@Composable
@Preview(showBackground = true, widthDp = 800, heightDp = 400)
fun JoyStick() {
    val parentBackgroundColor = TertiaryColor
    val hoverAreaColor = parentBackgroundColor.copy(alpha = 0.8f).let {
        Color(
            red = (it.red * 0.8f).toFloat(),
            green = (it.green * 0.8f).toFloat(),
            blue = (it.blue * 0.8f).toFloat(),
            alpha = 0.4f // Adjust transparency here
        )
    }
    val buttonColor = parentBackgroundColor.copy(alpha = 0.8f).let {
        Color(
            red = (it.red * 0.8f).toFloat(),
            green = (it.green * 0.8f).toFloat(),
            blue = (it.blue * 0.8f).toFloat(),
            alpha = 0.6f // Adjust transparency here
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
                    detectDragGestures { change, dragAmount ->
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
                color = hoverAreaColor,
                radius = size.minDimension / 2,
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