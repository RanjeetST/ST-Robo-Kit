package com.example.strobokit.views.SplashAnimation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.strobokit.ui.theme.SmokeColor
import com.example.strobokit.utilities.dpToPx
import com.example.strobokit.utilities.randomInRange
import com.example.strobokit.utilities.toPx


@Composable
fun Explosion(progress: Float,carYPosition : Float) {
    val sizeDp = 200.dp
    val sizePx = sizeDp.toPx()
    val sizePxHalf = sizePx / 2
    val particles = remember {
        List(30) {
            Particle(
                color = Color(listOf(
                    SmokeColor.toArgb(),
                    Color.LightGray.toArgb(),
                    Color.White.toArgb()).random()),
                startYPosition = sizePxHalf.toInt(),
                startXPosition = (sizePxHalf + carYPosition.dpToPx()).toInt() + 40.dp.toPx().toInt(), // Adjusted for car position
                maxHorizontalDisplacement = sizePx * randomInRange(-0.4f, 0.4f),
                maxVerticalDisplacement = sizePx * randomInRange(0f, 0f)
            )
        }
    }
    particles.forEach { it.updateProgress(progress) }

    Canvas(
        modifier = Modifier
            .size(sizeDp)
    ) {

        particles.forEach { particle ->
            drawCircle(
                alpha = particle.alpha,
                color = particle.color,
                radius = particle.currentRadius,
                center = Offset(particle.currentXPosition, particle.currentYPosition),
            )
        }
    }
}