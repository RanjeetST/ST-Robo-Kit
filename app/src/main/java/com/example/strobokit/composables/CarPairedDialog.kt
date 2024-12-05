package com.example.strobokit.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.R
import com.example.strobokit.ui.theme.OnPrimary

@Composable
@Preview()
fun CarPairedDialog(){

    val painter = painterResource(id = R.drawable.bot_connected)

    Box(modifier = Modifier
        .width(337.dp)
        .height(449.dp)
        .padding(horizontal = 13.dp, vertical = 28.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(OnPrimary)
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()) {
            Text(
                text = stringResource(id = R.string.dialog_header), style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 22.4.sp,
                    fontWeight = FontWeight(700),
                    color = PrimaryColor,
                    textAlign = TextAlign.Center,
                )
            )

            Text(
                text = stringResource(id = R.string.dialog_text),
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 19.6.sp,
                    fontWeight = FontWeight(400),
                    color = PrimaryColor,
                    textAlign = TextAlign.Center,
                )
            )

            Image(painter = painter, contentDescription = "car",
                modifier = Modifier
                .fillMaxWidth(0.8f)
            )

            Surface(modifier = Modifier
                .clickable {}
                .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                .fillMaxWidth(0.8f),
                color = OnPrimary
            ) {
                Text(stringResource(id = R.string.dialog_option_homePage),color = Color(0xFF0047B2), fontSize = 15.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 12.dp), textAlign = TextAlign.Center)
            }

            Surface(modifier = Modifier
                .clickable {}
                .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                .fillMaxWidth(0.8f),
                color = PrimaryColor
            ) {
                Text(stringResource(id = R.string.dialog_option_startDriving),color = OnPrimary, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 12.dp), textAlign = TextAlign.Center)
            }

        }
    }
    
}