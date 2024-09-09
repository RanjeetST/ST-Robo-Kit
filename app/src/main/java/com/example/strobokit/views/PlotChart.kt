package com.example.strobokit.views

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
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
import com.example.strobokit.viewModels.PlotViewModel
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

@Composable
fun PlotChart(
    viewModel: PlotViewModel,
    navController: NavController,
    deviceId: String
) {
    var xLineData by remember { mutableStateOf(listOf<Point>()) }
    var yLineData by remember { mutableStateOf(listOf<Point>()) }
    var zLineData by remember { mutableStateOf(listOf<Point>()) }
    var maxLineData by remember { mutableStateOf(listOf<Point>()) }
    var minLineData by remember { mutableStateOf(listOf<Point>()) }

    var xValue by remember { mutableStateOf(0f) }
    var xAxisMinValue by remember { mutableStateOf(0f) }
    val visibleRange = 100f

    BackHandler {
        viewModel.disconnectFeature(deviceId,"Magnetometer")
        navController.popBackStack()
    }

    val xAxisData = AxisData.Builder()
        .steps(10)
        .axisStepSize(2.8.dp)
        .labelAndAxisLinePadding(5.dp)
        .build()

    val yMinValue by remember {
        derivedStateOf {
            minOf(
                xLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f,
                yLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f,
                zLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f,
                minLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y }?:0f,
            )
        }
    }
    val yMaxValue by remember {
        derivedStateOf {
            maxOf(
                xLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f,
                yLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f,
                zLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f,
                maxLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y }?:0f
            )
        }
    }


    val yAxisData = AxisData.Builder()
        .steps(21)
        .labelData { index ->
            val range = yMaxValue - yMinValue
            val numberOfSteps = range/100
            val stepSize = range / numberOfSteps
            (yMinValue + index * stepSize).toInt().toString()
        }
        .axisLineColor(Color.Black)
        .axisLabelColor(Color.Gray)
        .axisLabelFontSize(12.sp)
        .build()


    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = minLineData.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = Color.Transparent, width = 2.5f, lineType = LineType.Straight())
                ),
                Line(
                    dataPoints = xLineData.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = Color.Blue, width = 2.5f, lineType = LineType.Straight())
                ),
                Line(
                    dataPoints = yLineData.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = Color.Red, width = 2.5f, lineType = LineType.Straight())
                ),
                Line(
                    dataPoints = zLineData.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = Color.Green, width = 2.5f, lineType = LineType.Straight())
                ),
                Line(
                    dataPoints = maxLineData.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = Color.Transparent, width = 2.5f, lineType = LineType.Straight())
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(enableVerticalLines = false),
        backgroundColor = Color.White,
        isZoomAllowed = false
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .background(OnPrimary)) {
        val logValue = viewModel.featureUpdates.value?.data?.logValue
        val values = logValue?.split(",")?.map { it.trim().toFloat() }
        val x = values?.get(0)
        val y = values?.get(1)
        val z = values?.get(2)
        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)) {
            Text(text = "X Axis $x", color = Color.Blue)
            Text(text = "Y Axis $y", color = Color.Red)
            Text(text = "Z Axis $z", color = Color.Green)
        }


        if (xLineData.isNotEmpty() && yLineData.isNotEmpty() && zLineData.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,) {
                Text(
                    text = "Accelerometer (mg)",
                    modifier = Modifier
                        .graphicsLayer {
                            rotationZ = -90f
                            transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0f)
                        }
                        .layout { measurable, constraints ->
                            val placeable =
                                measurable.measure(constraints.copy(maxHeight = constraints.maxWidth))
                            layout(placeable.height, placeable.width) {
                                placeable.place(0, 0)
                            }
                        },
                    fontSize = 14.sp,
                    color = Color.Black,
                )

                LineChart(
                    modifier = Modifier
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
                val tempMax = (ceil(maxOf(x,y,z)/100)*100).toFloat()
                val tempMin = (floor(minOf(x,y,z)/100)*100).toFloat()
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

    LaunchedEffect(true) {
        viewModel.observeFeature(deviceId = deviceId, featureName = "Magnetometer")
    }
}
