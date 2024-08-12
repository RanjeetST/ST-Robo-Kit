package com.example.strobokit.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.ui.theme.SecondaryColor
import com.example.strobokit.R


@Composable
fun HomeScreen(navController: NavController){
    // Use a Column to stack elements vertically
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = SecondaryColor)
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
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    navController.navigate("device_list")
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                    )
                ) {
                    Text("Discover", color = Color.White)
                }

                Text(text = "Connect Your Robot")
            }

            Spacer(modifier = Modifier.height(16.dp)) // Optional spacer to add space above the image


        }

        Column {
            // Image at the bottom
            Image(
                painter = painterResource(id = R.drawable.st_logo), // Replace with your image resource
                contentDescription = "Your Image Description",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp) // Add padding if needed
                    .size(80.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    // Use a Column to stack elements vertically
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = SecondaryColor)
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
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    // Action for the button click
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                    )
                ) {
                    Text("Discover", color = Color.White)
                }

                Text(text = "Connect Your Robot")
            }

            Spacer(modifier = Modifier.height(16.dp)) // Optional spacer to add space above the image


        }

        Column {
            // Image at the bottom
            Image(
                painter = painterResource(id = R.drawable.st_logo), // Replace with your image resource
                contentDescription = "Your Image Description",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp) // Add padding if needed
                    .size(80.dp)
            )
        }
    }
}