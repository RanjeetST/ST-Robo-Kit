package com.st.robotics.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.st.robotics.R
import com.st.robotics.ui.theme.OnPrimary
import com.st.robotics.ui.theme.PrimaryColor
import com.st.robotics.utilities.SessionManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController){
    val painter = painterResource(id = R.drawable.st_logo_clear)

    LaunchedEffect(Unit) {
        delay(1500)
        SessionManager.setSplashShown(true)
        navController.navigate("home"){
            popUpTo("splash_screen") {inclusive = true}
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OnPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .background(OnPrimary),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Image(painter = painter, contentDescription ="ST logo" , modifier = Modifier.size(100.dp))
            Text(text = "ST Robotics", color = PrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

    }


}