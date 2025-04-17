package com.st.robotics.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.st.robotics.ui.theme.PrimaryColor
import com.st.robotics.viewModels.FotaViewModel

@Composable
fun FotaScreen(
    viewModel: FotaViewModel,
    deviceId: String,
    navController: NavController
) {
    val context = LocalContext.current
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf<String?>(null) }
    val updateState = viewModel.fwUpdateState
    val CIRCULAR_TIMER_RADIUS = 20.dp

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            fileUri = it
            viewModel.onFileSelected(it,context)
            fileName = viewModel.selectedFileName
        }
    }

    LaunchedEffect(Unit) {
        viewModel.isFastFota(nodeId = deviceId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .padding(32.dp)
                .requiredHeight(CIRCULAR_TIMER_RADIUS)
        ) {
            inset(size.width / 2 - 20f, size.height / 2 - 20f) {
                drawCircle(
                    color = Color.White,
                    radius = 20f,
                    center = center,
                )

                updateState.value.progress?.let {
                    drawArc(
                        startAngle = 270f,
                        sweepAngle = it,
                        useCenter = false,
                        color = PrimaryColor
                    )
                }
            }
        }
        Button(
            onClick = {
                filePickerLauncher.launch("application/octet-stream")
            }
        ) {
            Text("Select .bin or .img File")
        }

        Spacer(modifier = Modifier.height(16.dp))

        fileName?.let {
            Text("Selected File: $it")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.onSubmit(deviceId)
            },
            enabled = fileUri != null
        ) {
            Text("Submit")
        }
    }
}