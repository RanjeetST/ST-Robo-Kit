package com.st.robotics.views

import androidx.compose.foundation.background
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.st.blue_sdk.features.extended.scene_description.SceneDescription
import com.st.robotics.R
import com.st.robotics.ui.theme.OnPrimary
import com.st.robotics.ui.theme.PrimaryColor
import com.st.robotics.ui.theme.ST_Magenta
import com.st.robotics.ui.theme.ST_Maroon
import com.st.robotics.ui.theme.SecondaryColor
import com.st.robotics.viewModels.SceneDescriptorViewModel

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
            horizontalAlignment = Alignment.Start
        ) {
            Text(stringResource(id = R.string.ToFData), color = Color.Black, fontSize = 18.sp , fontWeight = FontWeight.Bold)
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ){
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .background(
                                    ST_Maroon,
                                    shape = RoundedCornerShape(6.dp)
                                )
                        )
                        Text(" Maroon (0-499),", fontSize = 12.sp, color = PrimaryColor, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .background(
                                    SecondaryColor,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        Text(" Yellow (500-999)", fontSize = 12.sp, color = PrimaryColor, fontWeight = FontWeight.SemiBold)
                    }
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .background(
                                    PrimaryColor,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        Text(" Dark Blue (1000-3999), ", fontSize = 12.sp, color = PrimaryColor, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .background(
                                    Color.Black,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        Text(" Black (4000 and above)", fontSize = 12.sp, color = PrimaryColor, fontWeight = FontWeight.SemiBold)
                    }
                }
                Text("\uD83D\uDCCC Transparency indicates intensity - lower values in each range appear more solid.", fontSize = 12.sp, color = PrimaryColor)
            }
        }

        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
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
                                value < 500 -> {
                                    val alphaValue = maxOf(1f - (value.toFloat() / 500f), 0.4f)
                                    ST_Maroon.copy(alpha = alphaValue)
                                }
                                value in 500..999 -> {
                                    val alphaValue = maxOf(1f - ((value - 500).toFloat() / 500f), 0.4f)
                                    SecondaryColor.copy(alpha = alphaValue)
                                }
                                value in 1000..4000 -> {
                                    val alphaValue = maxOf(1f - ((value - 1000).toFloat() / 3000f), 0.4f)
                                    PrimaryColor.copy(alpha = alphaValue)
                                }
                                else -> Color.Black
                            }
                            Surface(
                                modifier = Modifier
                                    .height(36.dp)
                                    .width(40.dp)
                                ,
                                color = color,
                                shape = RoundedCornerShape(4.dp),

                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "$value",
                                        color = OnPrimary,
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.sp
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