package com.st.robotics.views

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.st.blue_sdk.features.extended.scene_description.SceneDescription
import com.st.robotics.R
import com.st.robotics.ui.theme.OnPrimary
import com.st.robotics.viewModels.SceneDescriptorViewModel

import androidx.compose.runtime.*

@Composable
fun SceneDescriptor(
    viewModel: SceneDescriptorViewModel,
    deviceId: String
){
    LaunchedEffect(Unit) {
        viewModel.observeFeature(deviceId = deviceId, featureName = SceneDescription.NAME)
    }

    // Remember the previous non-null value of sampleArray
    var previousSampleArray by remember { mutableStateOf<List<List<Long>>?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(OnPrimary)
    ) {
        val logValue = viewModel.featureUpdates.value?.payload?.value
        val sampleArray = logValue?.tofZones

        // Update previousSampleArray only if sampleArray is not null
        if (sampleArray != null) {
            previousSampleArray = sampleArray
        }

        Column(modifier = Modifier
            .background(OnPrimary)
            .fillMaxWidth()
            .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(id = R.string.ToFData), color = Color.Black)
        }

        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f),
            verticalArrangement = Arrangement.Bottom
        ) {
            val arrayToDisplay = previousSampleArray

            if (arrayToDisplay != null) {
                val rows = arrayToDisplay.size


                for (i in 0 until rows) {
                    val cols = arrayToDisplay[i].size ?: 0
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (j in 0 until cols) {
                            val value = arrayToDisplay[i][j]
                            val color = when {
                                value <= 1000 -> {
                                    val alphaValue = maxOf(value.toFloat() / 1000f, 0.4f)
                                    Color.Red.copy(alpha = alphaValue)
                                }
                                value in 1001..4000 -> {
                                    val alphaValue = maxOf(value.toFloat() / 4000, 0.4f)
                                    Color.Blue.copy(alpha = alphaValue)
                                }
                                else -> Color.Black
                            }
                            Surface(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(40.dp),
                                color = color,
                                shape = RoundedCornerShape(10.dp),
                            ) {
                                Text(
                                    text = "$value",
                                    color = OnPrimary,
                                    textAlign = TextAlign.Center,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}