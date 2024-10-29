package com.example.strobokit.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.strobokit.R
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.utilities.SessionManager
import com.example.strobokit.views.splashAnimation.ControlledExplosion
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController){

    val carYPosition = remember { Animatable(0f) }
    val backgroundGradient = Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to PrimaryColor.copy(alpha = 0.9f),
            0.95f to PrimaryColor.copy(alpha = 0.7f),
            1f to PrimaryColor.copy(alpha = 0.6f)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    var visibility by remember { mutableStateOf(false) }
    var animationComplete by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(1000)
        carYPosition.animateTo(
            targetValue = -600f, // Move upwards
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
        visibility = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .background(brush = backgroundGradient),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.robo_car_launch),
            contentDescription = null,
            modifier = Modifier
                .offset(y = carYPosition.value.dp)
                .size(100.dp),
            contentScale = ContentScale.FillHeight
        )
        ControlledExplosion(carYPosition = carYPosition.value)
        AnimatedVisibility(
            visible = visibility,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
            exit = fadeOut(animationSpec = tween(durationMillis = 1000))
        ) {
            Text(text = "ST Robotics", fontSize = 40.sp, color = Color.White)
        }

    }

    LaunchedEffect(visibility) {
        if (visibility) {
            delay(1000)
            animationComplete = true
        }
    }

    LaunchedEffect(animationComplete) {
        if (animationComplete) {
            SessionManager.setSplashShown(true)
            navController.navigate("home") {
                popUpTo("splash_screen") { inclusive = true }
            }
        }
    }
}