package com.example.strobokit.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.strobokit.R
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.TertiaryColor

@Composable
fun FeatureBox(featureName: String){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp),
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 10.dp,
        color = TertiaryColor,
    ){
        Column(
            modifier= Modifier.fillMaxWidth()
                .padding(10.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painter = painterResource(R.drawable.controller),
                contentDescription = null,
                tint = OnPrimary,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            Text(text = featureName, fontSize = 15.sp, color = OnPrimary)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun FeatureBox(){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp),
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 10.dp,
        color = TertiaryColor,
    ){
        Column(
            modifier= Modifier
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painter = painterResource(R.drawable.controller),
                contentDescription = null,
                tint = OnPrimary,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            Text(text = "Debug Console",
                fontSize = 15.sp,
                color = OnPrimary,
                softWrap = true,
                maxLines = 2,
                textAlign = TextAlign.Center)
        }
    }
}