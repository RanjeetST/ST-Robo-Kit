package com.example.strobokit.views

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.viewModels.PlotViewModel
import com.st.blue_sdk.features.pressure.Pressure
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

@Composable
fun PlotChart(
    viewModel: PlotViewModel,
    navController: NavController,
    deviceId: String
) {
    var xLineData by remember { mutableStateOf(listOf<Point>()) }
    var yLineData by remember { mutableStateOf(listOf<Point>()) }
    var zLineData by remember { mutableStateOf(listOf<Point>()) }

    //to set max and min for Y axis
    var maxLineData by remember { mutableStateOf(listOf<Point>()) }
    var minLineData by remember { mutableStateOf(listOf<Point>()) }

    var xValue by remember { mutableStateOf(0f) }
    var xAxisMinValue by remember { mutableStateOf(0f) }
    val visibleRange = 100f

    val xAxisData = AxisData.Builder()
        .steps(10)
        .axisStepSize(2.8.dp)
        .labelAndAxisLinePadding(5.dp)
        .backgroundColor(Color.White)
        .build()

    val yMinValue by remember {
        derivedStateOf {
            minOf(
                xLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f,
                yLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f,
                zLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f,
                minLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f,
            )
        }
    }
    val yMaxValue by remember {
        derivedStateOf {
            maxOf(
                xLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f,
                yLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f,
                zLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f,
                maxLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f
            )
        }
    }

    val yAxisData = AxisData.Builder()
        .steps(maxOf(ceil((yMaxValue - yMinValue) / 100).toInt(), 10))
        .labelData { index ->
            val range = yMaxValue - yMinValue
            val numberOfSteps = maxOf((range / 100).toInt(), 10)
            val stepSize = range / numberOfSteps
            (yMinValue + index * stepSize).toInt().toString()
        }
        .axisLineColor(Color.Black)
        .axisLabelColor(Color.Gray)
        .backgroundColor(Color.White)
        .axisLabelFontSize(12.sp)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = minLineData.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = Color.Transparent, width = 1f, lineType = LineType.Straight())
                ),
                Line(
                    dataPoints = xLineData.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = Color.Blue, width = 3f, lineType = LineType.Straight())
                ),
                Line(
                    dataPoints = yLineData.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = Color.Red, width = 3f, lineType = LineType.Straight())
                ),
                Line(
                    dataPoints = zLineData.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = Color.Green, width = 3f, lineType = LineType.Straight())
                ),
                Line(
                    dataPoints = maxLineData.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = Color.Transparent, width = 1f, lineType = LineType.Straight())
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(enableVerticalLines = false),
        backgroundColor = Color.White,
        isZoomAllowed = false
    )

    val usernames = listOf("Gyroscope", "Magnetometer", "Accelerometer")
    val isDropDownExpanded = remember { mutableStateOf(false) }
    val itemPosition = remember { mutableStateOf(0) }
    val selectedFeature = remember { mutableStateOf(usernames[itemPosition.value]) }

    val featureUnits = mapOf("Gyroscope" to "dps", "Magnetometer" to "mGa", "Accelerometer" to "mg")

    BackHandler {
        viewModel.disconnectFeature(deviceId, selectedFeature.value)
        navController.popBackStack()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(OnPrimary)) {
        val logValue = viewModel.featureUpdates.value?.data?.logValue
        val values = logValue?.split(",")?.map { it.trim().toFloat() }
        val x = values?.get(0)
        val y = values?.get(1)
        val z = values?.get(2)

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
                androidx.compose.material.IconButton(onClick = {
                    viewModel.disconnectFeature(deviceId, selectedFeature.value)
                    navController.popBackStack()
                }) {
                    androidx.compose.material.Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = OnPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            androidx.compose.material3.Text(
                text = "Plot Data",
                fontSize = 20.sp,
                color = OnPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)) {
            Text(text = "X: $x,", color = Color.Blue)
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = "Y: $y,", color = Color.Red)
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = "Z: $z", color = Color.Green)
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        isDropDownExpanded.value = true
                    }
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(text = usernames[itemPosition.value], fontSize = 12.sp)
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Back", tint = PrimaryColor)
            }
            DropdownMenu(
                modifier = Modifier.fillMaxWidth().background(OnPrimary),
                expanded = isDropDownExpanded.value,
                onDismissRequest = {
                    isDropDownExpanded.value = false
                }) {
                usernames.forEachIndexed { index, username ->
                    DropdownMenuItem(text = {
                        Text(text = username)
                    },
                        onClick = {
                            isDropDownExpanded.value = false
                            itemPosition.value = index
                            selectedFeature.value = username
                        })
                }
            }
        }

        if (xLineData.isNotEmpty() && yLineData.isNotEmpty() && zLineData.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth().background(Color.White),
                verticalAlignment = Alignment.CenterVertically,) {
                Text(
                    text = "${selectedFeature.value} (${featureUnits[selectedFeature.value]})",
                    modifier = Modifier.rotateVertically(clockwise = false),
                    fontSize = 14.sp,
                    color = Color.Black,
                )

                LineChart(
                    modifier = Modifier
                        .background(OnPrimary)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    lineChartData = lineChartData
                )
            }
        }
    }

    LaunchedEffect(viewModel.featureUpdates.value?.data?.logValue) {
        val logValue = viewModel.featureUpdates.value?.data?.logValue
        if (!logValue.isNullOrEmpty()) {
            val values = logValue.split(",").map { it.trim().toFloat() }
            if (values.size == 3) {
                val (x, y, z) = values
                var tempMax = (ceil(maxOf(x, y, z) / 100) * 100).toFloat()
                var tempMin = (floor(minOf(x, y, z) / 100) * 100).toFloat()
                if(selectedFeature.value == "Gyroscope"){
                    tempMax = max(tempMax,1000f)
                    tempMin = min(tempMin,-1000f)
                }
                xLineData = xLineData + Point(xValue, x)
                yLineData = yLineData + Point(xValue, y)
                zLineData = zLineData + Point(xValue, z)
                maxLineData = maxLineData + Point(xValue, tempMax)
                minLineData = minLineData + Point(xValue, tempMin)

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
        maxLineData = emptyList()
        minLineData = emptyList()
        viewModel.observeFeature(deviceId = deviceId, featureName = selectedFeature.value)
    }
}

fun Modifier.rotateVertically(clockwise: Boolean = true): Modifier {
    val rotate = rotate(if (clockwise) 90f else -90f)

    val adjustBounds = layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }
    return rotate then adjustBounds
}