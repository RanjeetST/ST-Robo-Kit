package com.example.strobokit.views

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.sp
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.ui.theme.SecondaryColor
import com.example.strobokit.R
import com.example.strobokit.composables.CarModel
import com.example.strobokit.ui.theme.Magenta
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.TertiaryColor


@Composable
fun HomeScreen(navController: NavController){
    val backgroundGradient = Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to PrimaryColor.copy(alpha = 0.9f),  // Start with PrimaryColor
            0.95f to PrimaryColor.copy(alpha = 0.7f), // Keep PrimaryColor until 95% of the screen
            1f to PrimaryColor.copy(alpha = 0.6f)      // Transition to White at the bottom-right corner
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

    // Use a Column to stack elements vertically
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .background(brush = backgroundGradient)
    ) {
        // TopAppBar for the top bar
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

        // Spacer to add some space below the TopAppBar
        Spacer(modifier = Modifier.height(16.dp))

        // Main content area below the top bar
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

            Spacer(modifier = Modifier.height(50.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .background(buttonGradient, shape = RoundedCornerShape(6.dp)) // Apply gradient with shape here
                ) {
                    Button(
                        onClick = {
                            navController.navigate("device_list")
                            showCarModel = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent, // Transparent button color
                        ),
                        shape = RoundedCornerShape(6.dp), // Apply the shape to the button
                        contentPadding = PaddingValues(horizontal = 60.dp, vertical = 15.dp)
                    ) {
                        Text("Discover", color = Color.White, fontSize = 15.sp)
                    }
                }

                Text(text = "Connect Your Robot", color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(16.dp)) // Optional spacer to add space above the image

        }
    }
}


@Preview
@Composable
fun HomeScreenPreview(){
    val backgroundGradient = Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to PrimaryColor.copy(alpha = 0.9f),  // Start with PrimaryColor
            0.95f to PrimaryColor.copy(alpha = 0.7f), // Keep PrimaryColor until 95% of the screen
            1f to PrimaryColor.copy(alpha = 0.6f)      // Transition to White at the bottom-right corner
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

    // Use a Column to stack elements vertically
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .background(brush = backgroundGradient)
    ) {
        // TopAppBar for the top bar
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

        // Spacer to add some space below the TopAppBar
        Spacer(modifier = Modifier.height(16.dp))

        // Main content area below the top bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image at the top
            Image(
                painter = painterResource(id = R.drawable.robo_car), // Replace with your image resource
                contentDescription = "Your Image Description",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp) // Add padding if needed
                    .size(200.dp)
            )
            Spacer(modifier = Modifier.height(50.dp))
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .background(buttonGradient, shape = RoundedCornerShape(6.dp)) // Apply gradient with shape here
                ) {
                    Button(
                        onClick = {
                            // Action for the button click
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent, // Transparent button color
                        ),
                        shape = RoundedCornerShape(6.dp), // Apply the shape to the button
                        contentPadding = PaddingValues(horizontal = 60.dp, vertical = 15.dp)
                    ) {
                        Text("Discover", color = Color.White, fontSize = 15.sp)
                    }
                }

                Text(text = "Connect Your Robot", color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(16.dp)) // Optional spacer to add space above the image

        }
    }
}