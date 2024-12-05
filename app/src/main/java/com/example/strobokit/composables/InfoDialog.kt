package com.example.strobokit.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.strobokit.R
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.ui.theme.TertiaryColor

@Composable
@Preview
fun InfoDialog(){
    Box(modifier = Modifier
        .width(337.dp)
        .height(449.dp)
        .background(OnPrimary)
        .border(
            width = 2.dp,
            color = PrimaryColor,
            shape = RoundedCornerShape(8.dp)
        ),
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // This will space out the items evenly
            ) {
                Spacer(modifier = Modifier.weight(1.5f)) // This spacer will take up the left space
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor,
                    modifier = Modifier.weight(3f) // Adjust weight to ensure text is centered
                )
                IconButton(modifier = Modifier.fillMaxWidth(0.25f),onClick = { /*TODO*/ }) {
                    Icon(
                        modifier = Modifier.size(15.dp),
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = "close"
                    )
                }
            }
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "App Version: 1.0",color = TertiaryColor, fontSize = 12.sp)
                Text(text = "Build No. 1.0",color = TertiaryColor, fontSize = 10.sp)
            }
            Image(painter = painterResource(id = R.drawable.robokit_icon_foreground), contentDescription = "app icon")


            Surface(modifier = Modifier
                .clickable {
                }
                .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                .fillMaxWidth(0.8f),
                color = PrimaryColor
            ) {
                Text(text = "Privacy Policy",color = OnPrimary, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 6.dp), textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Surface(modifier = Modifier
                .clickable {
                }
                .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                .fillMaxWidth(0.8f),
                color = PrimaryColor
            ) {
                Text(text = "Help",color = OnPrimary, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 6.dp), textAlign = TextAlign.Center)
            }
        }
    }
}