package com.example.strobokit.views.SplashAnimation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@Composable
fun ControlledExplosion(carYPosition: Float) {
    val particleSystems = remember { mutableStateListOf<ParticleSystem>() }

    LaunchedEffect(carYPosition) {
        if (carYPosition != 0f) {
            particleSystems.add(ParticleSystem(carYPosition))
        }else if (carYPosition == 0f){
            delay(700)
            repeat(10){
                particleSystems.add(ParticleSystem(carYPosition))
            }
        }
    }

    Box {
        particleSystems.forEach { system ->
            system.update()
            Explosion(system.progress, system.carYPosition)
        }
    }
}

class ParticleSystem(val carYPosition: Float) {
    var progress by mutableFloatStateOf(0f)

    fun update() {
        if (progress < 1f) {
            progress += 0.01f
        }
    }
}