package com.st.robotics.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import com.st.blue_sdk.features.acceleration.Acceleration
import com.st.blue_sdk.features.extended.scene_description.SceneDescription
import com.st.blue_sdk.features.gyroscope.Gyroscope
import com.st.blue_sdk.features.magnetometer.Magnetometer
import com.st.robotics.ui.theme.OnPrimary
import com.st.robotics.ui.theme.PrimaryColor
import com.st.robotics.ui.theme.ST_Magenta
import com.st.robotics.ui.theme.SecondaryColor
import com.st.robotics.ui.theme.TertiaryColor
import com.st.robotics.viewModels.PlotViewModel
import com.st.robotics.views.SceneDescriptor
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

@Composable
fun ControllerSensorData(
    viewModel: PlotViewModel,
    navController: NavController,
    deviceId: String
) {
    var xLineData by remember { mutableStateOf(listOf<Point>()) }
    var yLineData by remember { mutableStateOf(listOf<Point>()) }
    var zLineData by remember { mutableStateOf(listOf<Point>()) }

    var isStart by remember {mutableStateOf(true)}

    var xValue by remember { mutableFloatStateOf(0f) }
    var xAxisMinValue by remember { mutableFloatStateOf(0f) }
    val visibleRange = 105f
    val MIN_NUMBER_OF_STEPS = 20

    val xAxisData = AxisData.Builder()
        .steps(5)
        .axisStepSize(3.dp)
        .labelAndAxisLinePadding(2.dp)
        .backgroundColor(PrimaryColor)
        .axisLineColor(Color.Transparent)
        .build()

    // Calculate tight Y-axis ranges with only 3 labels around actual data
    val yMinValue1 by remember {
        derivedStateOf {
            val minVal = xLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f
            val maxVal = xLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f
            val center = (minVal + maxVal) / 2
            center - 10f // Show 10 units below center
        }
    }
    val yMaxValue1 by remember {
        derivedStateOf {
            val minVal = xLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f
            val maxVal = xLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f
            val center = (minVal + maxVal) / 2
            center + 10f // Show 10 units above center
        }
    }

    val yMinValue2 by remember {
        derivedStateOf {
            val minVal = yLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f
            val maxVal = yLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f
            val center = (minVal + maxVal) / 2
            center - 10f
        }
    }
    val yMaxValue2 by remember {
        derivedStateOf {
            val minVal = yLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f
            val maxVal = yLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f
            val center = (minVal + maxVal) / 2
            center + 10f
        }
    }

    val yMinValue3 by remember {
        derivedStateOf {
            val minVal = zLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f
            val maxVal = zLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f
            val center = (minVal + maxVal) / 2
            center - 10f
        }
    }
    val yMaxValue3 by remember {
        derivedStateOf {
            val minVal = zLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f
            val maxVal = zLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f
            val center = (minVal + maxVal) / 2
            center + 10f
        }
    }

    val yAxisData1 = AxisData.Builder()
        .steps(3) // Fixed to 3 steps
        .labelData { index ->
            val range = yMaxValue1 - yMinValue1
            val stepSize = range / 2 // 2 intervals for 3 labels
            (yMinValue1 + index * stepSize).toInt().toString()
        }
        .axisLineColor(Color.Gray)
        .axisLabelColor(Color.Gray)
        .backgroundColor(PrimaryColor)
        .axisLabelFontSize(10.sp)
        .build()

    val yAxisData2 = AxisData.Builder()
        .steps(3) // Fixed to 3 steps
        .labelData { index ->
            val range = yMaxValue2 - yMinValue2
            val stepSize = range / 2 // 2 intervals for 3 labels
            (yMinValue2 + index * stepSize).toInt().toString()
        }
        .axisLineColor(Color.Gray)
        .axisLabelColor(Color.Gray)
        .backgroundColor(PrimaryColor)
        .axisLabelFontSize(10.sp)
        .build()

    val yAxisData3 = AxisData.Builder()
        .steps(3) // Fixed to 3 steps
        .labelData { index ->
            val range = yMaxValue3 - yMinValue3
            val stepSize = range / 2 // 2 intervals for 3 labels
            (yMinValue3 + index * stepSize).toInt().toString()
        }
        .axisLineColor(Color.Gray)
        .axisLabelColor(Color.Gray)
        .backgroundColor(PrimaryColor)
        .axisLabelFontSize(10.sp)
        .build()

    // Simplified LineChartData - only use actual data points
    val lineChartData1 = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = xLineData.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = ST_Magenta, width = 4f, lineType = LineType.Straight())
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData1,
        gridLines = GridLines(
            enableVerticalLines = false,
            drawHorizontalLines = { xStart, y, xEnd ->
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    start = Offset(xStart, y),
                    end = Offset(xEnd, y)
                )
            }
        ),
        backgroundColor = PrimaryColor,
        isZoomAllowed = false,
        paddingRight = 0.dp,
        containerPaddingEnd = 0.dp
    )

    val lineChartData2 = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = yLineData.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = SecondaryColor, width = 4f, lineType = LineType.Straight())
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData2,
        gridLines = GridLines(
            enableVerticalLines = false,
            drawHorizontalLines = { xStart, y, xEnd ->
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    start = Offset(xStart, y),
                    end = Offset(xEnd, y)
                )
            }
        ),
        backgroundColor = PrimaryColor,
        isZoomAllowed = false,
        paddingRight = 0.dp,
        containerPaddingEnd = 0.dp
    )

    val lineChartData3 = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = zLineData.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = TertiaryColor, width = 4f, lineType = LineType.Straight())
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData3,
        gridLines = GridLines(
            enableVerticalLines = false,
            drawHorizontalLines = { xStart, y, xEnd ->
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    start = Offset(xStart, y),
                    end = Offset(xEnd, y)
                )
            }
        ),
        backgroundColor = PrimaryColor,
        isZoomAllowed = false,
        paddingRight = 0.dp,
        containerPaddingEnd = 0.dp
    )

    val featureName = listOf(Acceleration.NAME,Gyroscope.NAME, Magnetometer.NAME)
    val isDropDownExpanded = remember { mutableStateOf(false) }
    val itemPosition = remember { mutableIntStateOf(0) }
    val selectedFeature = remember { mutableStateOf(featureName[itemPosition.intValue]) }

    val featureUnits = mapOf("Gyroscope" to "dps", "Magnetometer" to "mGa", "Accelerometer" to "mg")

    var lastX by remember { mutableStateOf<Float?>(null) }
    var lastY by remember { mutableStateOf<Float?>(null) }
    var lastZ by remember { mutableStateOf<Float?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Transparent)
    ) {
        val logValue = viewModel.featureUpdates.value?.data?.logValue

        val values = logValue?.split(",")?.map { it.trim().toFloat() }
        val x = if (isStart) values?.get(0) else lastX
        val y = if (isStart) values?.get(1) else lastY
        val z = if (isStart) values?.get(2) else lastZ

        if (isStart) {
            // Update the last known values
            if (x != null) lastX = x
            if (y != null) lastY = y
            if (z != null) lastZ = z
        }

        Row {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {

                Row(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,

                    ) {
                    Box(
                        modifier = Modifier
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    isDropDownExpanded.value = true
                                }
                                .padding(10.dp)
                        ) {
                            Text(text = featureName[itemPosition.value], fontSize = 12.sp,color = OnPrimary)
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Back", tint = OnPrimary)
                        }
                        DropdownMenu(
                            modifier = Modifier.fillMaxWidth(0.2f).background(OnPrimary),
                            expanded = isDropDownExpanded.value,
                            onDismissRequest = {
                                isDropDownExpanded.value = false
                            }) {
                            featureName.forEachIndexed { index, sensorName ->
                                DropdownMenuItem(text = {
                                    Text(text = sensorName,color = Color.Black,fontSize = 12.sp)
                                },
                                    onClick = {
                                        isDropDownExpanded.value = false
                                        itemPosition.value = index
                                        selectedFeature.value = sensorName
                                        isStart = true
                                    })
                            }
                        }
                    }
                    IconButton(onClick = { isStart = !isStart }) {
                        if(!isStart){
                            Icon(
                                Icons.Filled.PlayCircle,
                                contentDescription = "play",
                                tint = ST_Magenta
                            )
                        }else{
                            Icon(
                                Icons.Filled.StopCircle,
                                contentDescription = "pause",
                                tint = ST_Magenta
                            )
                        }
                    }
                }

                if (selectedFeature.value != SceneDescription.NAME &&xLineData.isNotEmpty() && yLineData.isNotEmpty() && zLineData.isNotEmpty()) {
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center) {

                        Text(text = "X: $x ${featureUnits[selectedFeature.value]},", color = ST_Magenta, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "Y: $y ${featureUnits[selectedFeature.value]},", color = SecondaryColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "Z: $z ${featureUnits[selectedFeature.value]}", color = TertiaryColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (selectedFeature.value != SceneDescription.NAME &&xLineData.isNotEmpty() && yLineData.isNotEmpty() && zLineData.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxHeight(0.5f).fillMaxWidth()) {
                    Row(modifier = Modifier
                        .fillMaxWidth(0.5f).padding(2.dp)
                        .background(Color.White)
                        .fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically,) {

                        LineChart(
                            modifier = Modifier
                                .background(PrimaryColor)
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            lineChartData = lineChartData1
                        )
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth().padding(2.dp)
                        .background(Color.White)
                        .fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically,) {

                        LineChart(
                            modifier = Modifier
                                .background(PrimaryColor)
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            lineChartData = lineChartData2
                        )
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth(0.5f).padding(2.dp)
                        .background(Color.White),
                        verticalAlignment = Alignment.CenterVertically,) {

                        LineChart(
                            modifier = Modifier
                                .background(PrimaryColor)
                                .fillMaxWidth(),
                            lineChartData = lineChartData3
                        )
                    }
                }
            }

        }else if(selectedFeature.value == SceneDescription.NAME){
            viewModel.disconnectFeature(deviceId,selectedFeature.value)
            SceneDescriptor(viewModel = hiltViewModel(),deviceId = deviceId)
        }
    }

    LaunchedEffect(viewModel.featureUpdates.value?.data?.logValue) {
        val logValue = viewModel.featureUpdates.value?.data?.logValue
        if (!logValue.isNullOrEmpty() && isStart) {
            val values = logValue.split(",").map { it.trim().toFloat() }
            if (values.size == 3) {
                val (x, y, z) = values

                // Simply add the actual data points without artificial buffer
                xLineData = xLineData + Point(xValue, x)
                yLineData = yLineData + Point(xValue, y)
                zLineData = zLineData + Point(xValue, z)

                xValue += 1f
                if (xValue > visibleRange) {
                    xAxisMinValue += 1f
                }
            }
        }
    }

    LaunchedEffect(selectedFeature.value) {
        xLineData = emptyList()
        yLineData = emptyList()
        zLineData = emptyList()
        xValue = 0f
        xAxisMinValue = 0f

        viewModel.observeFeature(deviceId = deviceId, featureName = selectedFeature.value)
    }
}