package com.example.strobokit.views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.strobokit.composables.CarModel
import com.example.strobokit.ui.theme.Magenta
import com.example.strobokit.ui.theme.PrimaryColor


@SuppressLint("ServiceCast")
@Composable
fun HomeScreen(navController: NavController){

    val backgroundGradient = Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to PrimaryColor.copy(alpha = 0.9f),
            0.95f to PrimaryColor.copy(alpha = 0.7f),
            1f to PrimaryColor.copy(alpha = 0.6f)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    val buttonGradient = Brush.horizontalGradient(
        0.0f to PrimaryColor,
        1.0f to Magenta,
        startX = 0.0f,
        endX = 500.0f
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .background(brush = backgroundGradient)
    ) {
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(align = Alignment.Center)
                ) {
                    Text("Devices")
                }
            },
            backgroundColor = PrimaryColor,
            contentColor = Color.White
        )

        Spacer(
            modifier = Modifier
                .height(16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var showCarModel by remember { mutableStateOf(true) }

            if (showCarModel) {
                CarModel()
            }

            Spacer(
                modifier = Modifier
                    .height(50.dp)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .background(buttonGradient, shape = RoundedCornerShape(6.dp))
                ) {
                    Button(
                        onClick = {
                            navController.navigate("device_list")
                            showCarModel = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 60.dp, vertical = 15.dp)
                    ) {
                        Text("Discover", color = Color.White, fontSize = 15.sp)
                    }
                }

                Text(text = "Connect Your Robot", color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}