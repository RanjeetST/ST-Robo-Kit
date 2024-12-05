package com.example.strobokit.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.strobokit.ui.theme.OnPrimary
import kotlin.random.Random
import com.example.strobokit.R

@Preview(showBackground = true)
@Composable
fun SceneDescriptor(){
    
    val sampleArray = Array(8) { IntArray(8) }

    for (i in sampleArray.indices) {
        for (j in sampleArray[i].indices) {
            sampleArray[i][j] = Random.nextInt(0, 6000)
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(OnPrimary)
    ) {

        Column(modifier = Modifier
            .background(OnPrimary)
            .fillMaxWidth()
            .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(id = R.string.ToFData),color = Color.Black)
        }

        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f),
            verticalArrangement = Arrangement.Bottom
        ) {
            for (i in 0..7)
            {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (j in 0 .. 7)
                    {
                        val color = if(sampleArray[i][j] <= 1000){
                            val alphaValue = maxOf(sampleArray[i][j].toFloat()/1000f,0.4f)
                            Color.Red.copy(alpha = alphaValue)
                        }else if(sampleArray[i][j] in 1001..4000){
                            val alphaValue = maxOf(sampleArray[i][j].toFloat()/4000,0.4f)
                            Color.Blue.copy(alpha = alphaValue)
                        }else{
                            Color.Black
                        }
                        Surface(
                            modifier = Modifier
                                .height(40.dp)
                                .width(40.dp)
                            ,
                            color = color,
                            shape = RoundedCornerShape(10.dp),
                        ){
                            Text(text = "${sampleArray[i][j]}", color = OnPrimary, textAlign = TextAlign.Center, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}