package com.example.strobokit.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.strobokit.R
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.utilities.SessionManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(PrimaryColor),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(id = R.drawable.st_logo),
            contentDescription = "ST Logo",
            modifier = Modifier.wrapContentSize()
        )
    }

    LaunchedEffect(Unit) {
        delay(2*1000)
        SessionManager.setSplashShown(true)
        navController.navigate("home"){
            popUpTo("splash_screen") {inclusive = true}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview(){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(PrimaryColor),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(id = R.drawable.st_logo),
            contentDescription = "ST Logo",
            modifier = Modifier.wrapContentSize()
        )
    }
}