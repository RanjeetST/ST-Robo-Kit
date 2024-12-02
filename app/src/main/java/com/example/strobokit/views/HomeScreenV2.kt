package com.example.strobokit.views

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.strobokit.R
import com.example.strobokit.composables.FeatureBox
import com.example.strobokit.ui.theme.Magenta
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.PrimaryColor


@SuppressLint("ServiceCast")
@Composable
fun HomeScreenV2(navController: NavController){

    Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to PrimaryColor.copy(alpha = 0.9f),
            0.95f to PrimaryColor.copy(alpha = 0.7f),
            1f to PrimaryColor.copy(alpha = 0.6f)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    val painter = painterResource(id = R.drawable.idle_bot)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
    ) {
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(align = Alignment.Center)
                ) {
                    Image(painter = painterResource(id = R.drawable.st_logo_clear), contentDescription = "ST_LOGO", modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center))
                }
            },
            backgroundColor = Color.White,
        )

        Spacer(
            modifier = Modifier
                .height(16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Box{
                Image(painter = painter, contentDescription = "car", contentScale = ContentScale.Crop, modifier = Modifier
                    .size(220.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(8.dp),
                        ambientColor = Color.Gray,
                        spotColor = Color.Gray
                    )
                )
            }

            Spacer(
                modifier = Modifier
                    .height(50.dp)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                    Surface(modifier = Modifier
                        .clickable {
                            navController.navigate("device_list")
                        }
                        .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                        .fillMaxWidth(0.8f),
                        color = PrimaryColor
                    ) {
                        androidx.compose.material3.Text(stringResource(id = R.string.pair_your_robot),color = OnPrimary, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 12.dp), textAlign = TextAlign.Center)
                    }

            }
        }
        Box(modifier = Modifier
            .background(PrimaryColor)
            .fillMaxHeight(),
            contentAlignment = Alignment.CenterEnd) {
            val scrollState = rememberScrollState()
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 10.dp)
                    .scrollable(
                        state = scrollState,
                        orientation = Orientation.Horizontal
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val itemNames =  listOf("Home","Controller","Monitor","Debug")

                itemsIndexed(items = itemNames) { _, item ->

                    if(item == "Home" || item == "Controller" || item == "Monitor" || item == "Debug"){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clickable { }
                                .align(Alignment.Center),

                        ) {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(horizontal = 20.dp)
                                .background(Color.Transparent),
                            ){
                                var iconImage : Int = R.drawable.controller

                                when (item) {
                                    "Debug" -> {
                                        iconImage = R.drawable.debug
                                    }
                                    "Home" -> {
                                        iconImage = R.drawable.home
                                    }
                                    "Controller" -> {
                                        iconImage = R.drawable.controller
                                    }
                                    "Monitor" -> {
                                        iconImage = R.drawable.plot
                                    }
                                }
                                Column(
                                    modifier= Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                    ,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Icon(painter = painterResource(iconImage),
                                        contentDescription = null,
                                        tint = if (item == "Home") OnPrimary else OnPrimary.copy(alpha = 0.4f),
                                        modifier = Modifier.size(25.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.height(5.dp))

                                    Text(
                                        text = item,
                                        color = OnPrimary,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .size(45.dp)
                                            .align(Alignment.CenterHorizontally) // This centers the text horizontally
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}