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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.strobokit.ui.theme.ErrorColor
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.ui.theme.TertiaryColor
import kotlin.random.Random

@Preview(showBackground = true)
@Composable
fun SceneDescriptor(){

    // Create an 8x8 array
    val array = Array(8) { IntArray(8) }

    // Fill the array with random values ranging from 0 to 4000
    for (i in array.indices) {
        for (j in array[i].indices) {
            array[i][j] = Random.nextInt(0, 6000)
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)
    ) {

        Column(modifier = Modifier
            .background(Color.LightGray)
            .fillMaxHeight(0.5f)
            .fillMaxWidth()
            .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Screen Descriptor")
            Text(text = "The screen descriptor feature utilizing an 8x8 matrix to display Time-of-Flight (ToF) data is an efficient way to visualize distance measurements. Each cell in the matrix represents a specific area of the sensor's field of view, providing a detailed grid of distance information. This allows for precise mapping and detection of objects within the sensor's range, making it useful for applications in robotics, gesture recognition, and environmental mapping. The compact 8x8 format ensures a balance between resolution and processing efficiency.", letterSpacing = 2.sp, lineHeight = 20.sp)
        }

        Column(modifier = Modifier
            .fillMaxWidth(),
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
                        val color = if(array[i][j] <= 1000){
                            val alphaValue = maxOf(array[i][j].toFloat()/1000f,0.4f)
                            Color.Red.copy(alpha = alphaValue)
                        }else if(array[i][j] in 1001..4000){
                            val alphaValue = maxOf(array[i][j].toFloat()/4000,0.4f)
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
                            shape = RoundedCornerShape(8.dp),
                        ){
                            Text(text = "${array[i][j]}", color = OnPrimary, textAlign = TextAlign.Center, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}