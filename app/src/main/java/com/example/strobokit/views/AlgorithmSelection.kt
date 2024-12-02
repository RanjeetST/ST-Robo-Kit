package com.example.strobokit.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.strobokit.R
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.viewModels.AlgorithmSelectionViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlgorithmSelection(
    deviceId : String,
    viewModel: AlgorithmSelectionViewModel,
    navController : NavController
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OnPrimary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryColor),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                androidx.compose.material.IconButton(onClick = {  navController.popBackStack()}) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = OnPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            androidx.compose.material3.Text(
                text = "Algorithm Selection",
                fontSize = 20.sp,
                color = OnPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 40.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(modifier = Modifier
                .align(Alignment.CenterHorizontally),
                fontSize = 20.sp,
                text = "Swipe to select and click on send"
            )

            val pagerState = rememberPagerState(pageCount = {
                3
            })

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                , horizontalAlignment = Alignment.CenterHorizontally
            ){
                HorizontalPager(modifier = Modifier
                    .fillMaxHeight(0.5f)
                    ,state = pagerState) { page ->
                    val command = when(page){
                        0 -> AlgorithmSelectionViewModel.Commands.FOLLOW_ME
                        1 -> AlgorithmSelectionViewModel.Commands.REMOTE_CONTROL
                        2 -> AlgorithmSelectionViewModel.Commands.FREE_NAVIGATION
                        else -> null
                    }
                    val commandText = when(command){
                        AlgorithmSelectionViewModel.Commands.FOLLOW_ME -> "Follow Me"
                        AlgorithmSelectionViewModel.Commands.REMOTE_CONTROL -> "Remote Control"
                        AlgorithmSelectionViewModel.Commands.FREE_NAVIGATION -> "Free Navigation"
                        null -> "None"
                    }
                    Box(modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        var painter : Int = R.drawable.controller

                        when (commandText) {
                            "Follow Me" -> {
                                painter = R.drawable.baseline_directions_walk_24
                            }
                            "Free Navigation" -> {
                                painter = R.drawable.baseline_assistant_navigation_24
                            }
                            "Remote Control" -> {
                                painter = R.drawable.baseline_border_outer_24
                            }
                        }
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                            .border(
                                BorderStroke(2.dp, Color.Black),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {

                            Row(modifier = Modifier
                                .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                Text(
                                    text = commandText,
                                    fontSize = 25.sp
                                )
                                Icon(painter = painterResource(painter),
                                    contentDescription = null,
                                    tint = PrimaryColor,
                                    modifier = Modifier.size(50.dp)
                                )
                            }


                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = PrimaryColor,
                                    contentColor = OnPrimary
                                ),
                                onClick = {
                                    if (command != null) {
                                        viewModel.sendCommand(command,deviceId)
                                    }
                                }) {
                                Text("Send", fontSize = 20.sp)
                            }
                        }
                    }
                }
                Row {
                    Icon(painter = painterResource(R.drawable.baseline_keyboard_arrow_left_24),
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(40.dp)
                    )
                    Icon(painter = painterResource(R.drawable.baseline_keyboard_arrow_right_24),
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun AlgorithmSelectionPreview(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OnPrimary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryColor),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                androidx.compose.material.IconButton(onClick = { }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = OnPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            androidx.compose.material3.Text(
                text = "Algorithm Selection",
                fontSize = 20.sp,
                color = OnPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 40.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(modifier = Modifier
                .align(Alignment.CenterHorizontally),
                fontSize = 20.sp,
                text = "Swipe to select and click on send"
            )

            val pagerState = rememberPagerState(pageCount = {
                3
            })

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                , horizontalAlignment = Alignment.CenterHorizontally
            ){
                HorizontalPager(modifier = Modifier
                    .fillMaxHeight(0.5f)
                    ,state = pagerState) { page ->
                    val command = when(page){
                        0 -> AlgorithmSelectionViewModel.Commands.FOLLOW_ME
                        1 -> AlgorithmSelectionViewModel.Commands.REMOTE_CONTROL
                        2 -> AlgorithmSelectionViewModel.Commands.FREE_NAVIGATION
                        else -> null
                    }
                    val commandText = when(command){
                        AlgorithmSelectionViewModel.Commands.FOLLOW_ME -> "Follow Me"
                        AlgorithmSelectionViewModel.Commands.FREE_NAVIGATION -> "Free Navigation"
                        AlgorithmSelectionViewModel.Commands.REMOTE_CONTROL -> "Remote Control"
                        null -> "None"
                    }
                    Box(modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        var painter : Int = R.drawable.controller

                        when (commandText) {
                            "Follow Me" -> {
                                painter = R.drawable.baseline_directions_walk_24
                            }
                            "Free Navigation" -> {
                                painter = R.drawable.baseline_assistant_navigation_24
                            }
                            "Remote Control" -> {
                                painter = R.drawable.baseline_border_outer_24
                            }
                        }
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                            .border(
                                BorderStroke(2.dp, Color.Black),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {

                            Row(modifier = Modifier
                                .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                Text(
                                    text = commandText,
                                    fontSize = 25.sp
                                )
                                Icon(painter = painterResource(painter),
                                    contentDescription = null,
                                    tint = PrimaryColor,
                                    modifier = Modifier.size(50.dp)
                                )
                            }


                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = PrimaryColor,
                                    contentColor = OnPrimary
                                ),
                                onClick = {
                                }) {
                                Text("Send", fontSize = 20.sp)
                            }
                        }
                    }
                }
                Row {
                    Icon(painter = painterResource(R.drawable.baseline_keyboard_arrow_left_24),
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(40.dp)
                    )
                    Icon(painter = painterResource(R.drawable.baseline_keyboard_arrow_right_24),
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}