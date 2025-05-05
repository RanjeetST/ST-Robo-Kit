package com.st.robotics.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.st.robotics.R
import com.st.robotics.composables.ProjectScreen
import com.st.robotics.ui.theme.OnPrimary
import com.st.robotics.ui.theme.PrimaryColor
import com.st.robotics.viewModels.ProfileScreenViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileScreenViewModel,
    navController: NavController
){

    val isUserLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val error by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                androidx.compose.material.IconButton(onClick = { navController.popBackStack() }) {
                    androidx.compose.material.Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = OnPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            androidx.compose.material3.Text(
                text = stringResource(id = R.string.sdDatasets),
                fontSize = 20.sp,
                color = OnPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }

        if(isUserLoggedIn){
            ProjectScreen(isUserLoggedIn,viewModel = hiltViewModel())
        }else{
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { viewModel.startScan()}) {
                    Text(text = "QR Code Scan")
                }
            }
        }
    }
}