package com.st.robotics.composables

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.st.robotics.R
import com.st.robotics.models.app.DataLog
import com.st.robotics.models.dataset.Status
import com.st.robotics.ui.theme.GreyColor
import com.st.robotics.ui.theme.PrimaryColor
import com.st.robotics.viewModels.DatalogViewModel
import com.st.robotics.viewModels.ProfileScreenViewModel
import dagger.hilt.android.qualifiers.ApplicationContext


@Composable
fun ProjectScreen(
    isUserLoggedIn: Boolean = false,
    viewModel: ProfileScreenViewModel,
    navController: NavController
){

    val context = ApplicationContext()
    val sampleProjectList by viewModel.projectList.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = isUserLoggedIn) {
        viewModel.fetchProjects()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ){
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                itemsIndexed(items = sampleProjectList){id , item ->
                    Box(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth()
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(4.dp),
                                clip = false
                            )
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White,
                                        PrimaryColor.copy(alpha = 0.6f)
                                    )
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 2.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(onClick = {
                                navController.navigate("project/${id}")
                            })

                    ){
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp,20.dp)) {
                            Text(text = "${item.name}", fontSize = 20.sp)
                            Row(modifier = Modifier
                                .fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {}) {
                                    Icon(painter = painterResource(id = R.drawable.baseline_discount_24), contentDescription = "tag", tint = PrimaryColor)
                                }
                                Text("Tags")
                            }

                            LazyRow(modifier = Modifier
                                .fillMaxSize()
                            ) {
                                itemsIndexed(item.allTags){_,tag->
                                    Box(modifier = Modifier
                                        .padding(2.dp)
                                        .background(
                                            GreyColor.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    ){
                                        Column(modifier = Modifier
                                            .padding(4.dp)) {
                                            Text(tag)
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}