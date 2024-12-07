package com.example.strobokit.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.strobokit.R
import com.example.strobokit.ui.theme.OnPrimary

@Composable
fun FeatureBox(featureName: String,iconSize: Int = 50,textSize:Int = 12,textAreaSize : Int = 70){
    var painter : Int = R.drawable.controller

    if(featureName == "Remote Control"){
        painter = R.drawable.controller
    }else if(featureName == "Plot Data"){
        painter = R.drawable.baseline_auto_graph_24
    }else if(featureName == "Debug"){
        painter = R.drawable.debug
    }else if(featureName == "Free navigation"){
        painter = R.drawable.baseline_assistant_navigation_24
    }else if(featureName == "Follow me"){
        painter = R.drawable.baseline_directions_walk_24
    }else if(featureName == "Edge detection"){
        painter = R.drawable.baseline_border_outer_24
    }else if(featureName == "Algorithm selection"){
        painter = R.drawable.baseline_border_outer_24
    }else if(featureName == "Scene descriptor"){
        painter = R.drawable.baseline_border_outer_24
    }else if(featureName == "Home"){
        painter = R.drawable.home
    }else if(featureName == "Controller"){
        painter = R.drawable.controller
    }else if(featureName == "Monitor"){
        painter = R.drawable.plot
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .background(Color.Transparent),
    ){
        Column(
            modifier= Modifier.fillMaxWidth()

            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painter = painterResource(painter),
                contentDescription = null,
                tint = OnPrimary,
                modifier = Modifier.size(iconSize.dp)
            )
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            Text(
                text = featureName,
                color = OnPrimary,
                fontSize = textSize.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .size(textAreaSize.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}