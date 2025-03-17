package com.st.robotics.views


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.st.robotics.R
import com.st.robotics.ui.theme.OnPrimary
import com.st.robotics.ui.theme.PrimaryColor
import com.st.robotics.ui.theme.ST_Magenta
import com.st.robotics.ui.theme.SecondaryColor
import com.st.robotics.ui.theme.TertiaryColor
import com.st.robotics.viewModels.PlotViewModel
import com.st.blue_sdk.features.acceleration.Acceleration
import com.st.blue_sdk.features.extended.scene_description.SceneDescription
import com.st.blue_sdk.features.gyroscope.Gyroscope
import com.st.blue_sdk.features.magnetometer.Magnetometer
import kotlin.math.ceil

@Composable
fun PlotChartV2(
    viewModel: PlotViewModel,
    navController: NavController,
    deviceId: String
) {
    var xLineData by remember { mutableStateOf(listOf<Point>()) }
    var yLineData by remember { mutableStateOf(listOf<Point>()) }
    var zLineData by remember { mutableStateOf(listOf<Point>()) }

    //to set max and min for Y axis
    var maxLineData1 by remember { mutableStateOf(listOf<Point>()) }
    var minLineData1 by remember { mutableStateOf(listOf<Point>()) }

    var maxLineData2 by remember { mutableStateOf(listOf<Point>()) }
    var minLineData2 by remember { mutableStateOf(listOf<Point>()) }

    var maxLineData3 by remember { mutableStateOf(listOf<Point>()) }
    var minLineData3 by remember { mutableStateOf(listOf<Point>()) }

    var isStart by remember {mutableStateOf(true)}

    var xValue by remember { mutableFloatStateOf(0f) }
    var xAxisMinValue by remember { mutableFloatStateOf(0f) }
    val visibleRange = 105f

    val xAxisData = AxisData.Builder()
        .steps(10)
        .axisStepSize(2.8.dp)
        .labelAndAxisLinePadding(5.dp)
        .backgroundColor(PrimaryColor)
        .axisLineColor(Color.Transparent)
        .build()

    val yMinValue1 by remember {
        derivedStateOf {
            minOf(
                xLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f,
                minLineData1.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f,
            )
        }
    }
    val yMaxValue1 by remember {
        derivedStateOf {
            maxOf(
                xLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f,
                maxLineData1.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f
            )
        }
    }

    val yMinValue2 by remember {
        derivedStateOf {
            minOf(
                yLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f,
                minLineData2.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f,
            )
        }
    }
    val yMaxValue2 by remember {
        derivedStateOf {
            maxOf(
                yLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f,
                maxLineData2.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f
            )
        }
    }

    val yMinValue3 by remember {
        derivedStateOf {
            minOf(
                zLineData.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f,
                minLineData3.takeLast(visibleRange.toInt()).minOfOrNull { it.y } ?: 0f,
            )
        }
    }
    val yMaxValue3 by remember {
        derivedStateOf {
            maxOf(
                zLineData.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f,
                maxLineData3.takeLast(visibleRange.toInt()).maxOfOrNull { it.y } ?: 0f
            )
        }
    }

    val yAxisData1 = AxisData.Builder()
        .steps(maxOf(ceil((yMaxValue1 - yMinValue1) / 500).toInt(), 5))
        .labelData { index ->
            val range = yMaxValue1 - yMinValue1
            val numberOfSteps = maxOf((range / 500).toInt(), 5)
            val stepSize = range / numberOfSteps
            (yMinValue1 + index * stepSize).toInt().toString()
        }
        .axisLineColor(Color.Gray)
        .axisLabelColor(Color.Gray)
        .backgroundColor(PrimaryColor)
        .axisLabelFontSize(12.sp)
        .build()

    val yAxisData2 = AxisData.Builder()
        .steps(maxOf(ceil((yMaxValue2 - yMinValue2) / 500).toInt(), 5))
        .labelData { index ->
            val range = yMaxValue2 - yMinValue2
            val numberOfSteps = maxOf((range / 500).toInt(), 5)
            val stepSize = range / numberOfSteps
            (yMinValue2 + index * stepSize).toInt().toString()
        }
        .axisLineColor(Color.Gray)
        .axisLabelColor(Color.Gray)
        .backgroundColor(PrimaryColor)
        .axisLabelFontSize(12.sp)
        .build()

    val yAxisData3 = AxisData.Builder()
        .steps(maxOf(ceil((yMaxValue3 - yMinValue3) / 500).toInt(), 5))
        .labelData { index ->
            val range = yMaxValue3 - yMinValue3
            val numberOfSteps = maxOf((range / 500).toInt(), 5)
            val stepSize = range / numberOfSteps
            (yMinValue3 + index * stepSize).toInt().toString()
        }
        .axisLineColor(Color.Gray)
        .axisLabelColor(Color.Gray)
        .backgroundColor(PrimaryColor)
        .axisLabelFontSize(12.sp)
        .build()

    //USED 3 LINES TO SET THE MAX,MIN & ACTUAL DATA OF THE CHART BEING PLOTTED
    val lineChartData1 = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = minLineData1.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = Color.Transparent, width = 1f, lineType = LineType.Straight())
                ),
                Line(
                    dataPoints = xLineData.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = ST_Magenta, width = 6f, lineType = LineType.Straight())
                ),
                Line(
                    dataPoints = maxLineData1.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = Color.Transparent, width = 1f, lineType = LineType.Straight())
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
        isZoomAllowed = false
    )

    val lineChartData2 = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = minLineData2.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = Color.Transparent, width = 1f, lineType = LineType.Straight())
                ),
                Line(
                    dataPoints = yLineData.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = SecondaryColor, width = 6f, lineType = LineType.Straight())
                ),
                Line(
                    dataPoints = maxLineData2.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = Color.Transparent, width = 1f, lineType = LineType.Straight())
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
        isZoomAllowed = false
    )

    val lineChartData3 = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = minLineData3.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = Color.Transparent, width = 1f, lineType = LineType.Straight())
                ),
                Line(
                    dataPoints = zLineData.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = TertiaryColor, width = 6f, lineType = LineType.Straight())
                ),
                Line(
                    dataPoints = maxLineData3.takeLast(visibleRange.toInt()),
                    lineStyle = LineStyle(color = Color.Transparent, width = 1f, lineType = LineType.Straight())
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
        isZoomAllowed = false
    )

    val featureName = listOf(Acceleration.NAME,Gyroscope.NAME, Magnetometer.NAME,SceneDescription.NAME)
    val itemPosition = remember { mutableIntStateOf(0) }
    val selectedFeature = remember { mutableStateOf(featureName[itemPosition.intValue]) }

    val featureUnits = mapOf("Gyroscope" to "dps", "Magnetometer" to "mGa", "Accelerometer" to "mg")

    BackHandler {
        viewModel.disconnectFeature(deviceId, selectedFeature.value)
        navController.popBackStack()
    }

    var lastX by remember { mutableStateOf<Float?>(null) }
    var lastY by remember { mutableStateOf<Float?>(null) }
    var lastZ by remember { mutableStateOf<Float?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(OnPrimary)) {
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
                IconButton(onClick = {
                    viewModel.disconnectFeature(deviceId, selectedFeature.value)
                    navController.popBackStack()
                }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = OnPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            androidx.compose.material3.Text(
                text = stringResource(id = R.string.monitor),
                fontSize = 20.sp,
                color = OnPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row {
            Column(modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = selectedFeature.value, fontSize = 20.sp,color = PrimaryColor, fontWeight = FontWeight.Bold)
                Image(painter = painterResource(id = R.drawable.mems), contentDescription = "MEMS")
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
            Box {
                Column{
                    featureName.forEachIndexed { index, sensorName ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Surface(modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .clickable {
                                    selectedFeature.value = sensorName
                                    itemPosition.intValue = index
                                },
                                color = PrimaryColor,
                                elevation = 2.dp) {
                                if(sensorName == SceneDescription.NAME){
                                    Text(text = stringResource(id = R.string.ToF), modifier = Modifier.padding(10.dp),color = OnPrimary,fontSize = 12.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                                }else{
                                    Text(text = sensorName, modifier = Modifier.padding(10.dp),color = OnPrimary,fontSize = 12.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                                }
                            }
                            if(selectedFeature.value == sensorName){
                                IconButton(onClick = { isStart = !isStart }) {
                                    if(!isStart){
                                        Icon(
                                            Icons.Filled.PlayCircle,
                                            contentDescription = "play",
                                            tint = PrimaryColor
                                        )
                                    }else{
                                        Icon(
                                            Icons.Filled.StopCircle,
                                            contentDescription = "pause",
                                            tint = ST_Magenta
                                        )
                                    }
                                }
                            }else {
                                IconButton(onClick = { /*TODO*/ }) {

                                }
                            }
                        }
                    }
                }

            }
        }



        if (selectedFeature.value != SceneDescription.NAME &&xLineData.isNotEmpty() && yLineData.isNotEmpty() && zLineData.isNotEmpty()) {

            Row(modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .fillMaxHeight(0.33f),
                verticalAlignment = Alignment.CenterVertically,) {

                LineChart(
                    modifier = Modifier
                        .background(OnPrimary)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    lineChartData = lineChartData1
                )
            }

            Row(modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .fillMaxHeight(0.5f),
                verticalAlignment = Alignment.CenterVertically,) {


                LineChart(
                    modifier = Modifier
                        .background(OnPrimary)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    lineChartData = lineChartData2
                )
            }

            Row(modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
                verticalAlignment = Alignment.CenterVertically,) {


                LineChart(
                    modifier = Modifier
                        .background(OnPrimary)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    lineChartData = lineChartData3
                )
            }
        }else if(selectedFeature.value == SceneDescription.NAME){
            SceneDescriptor(viewModel = hiltViewModel(),deviceId = deviceId)
        }
    }

    LaunchedEffect(viewModel.featureUpdates.value?.data?.logValue) {
        val logValue = viewModel.featureUpdates.value?.data?.logValue
        if (!logValue.isNullOrEmpty() && isStart) {
            val values = logValue.split(",").map { it.trim().toFloat() }
            if (values.size == 3) {
                val (x, y, z) = values
                var buffer = 500f

                if(selectedFeature.value == Gyroscope.NAME){
                    buffer = 1000f
                }

                xLineData = xLineData + Point(xValue, x)
                yLineData = yLineData + Point(xValue, y)
                zLineData = zLineData + Point(xValue, z)

                maxLineData1 = maxLineData1 + Point(xValue, x + buffer)
                minLineData1 = minLineData1 + Point(xValue, x - buffer)

                maxLineData2 = maxLineData2 + Point(xValue, y + buffer)
                minLineData2 = minLineData2 + Point(xValue, y - buffer)

                maxLineData3 = maxLineData3 + Point(xValue,z + buffer)
                minLineData3 = minLineData3 + Point(xValue, z - buffer)

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
        maxLineData1 = emptyList()
        minLineData1 = emptyList()
        maxLineData2 = emptyList()
        minLineData2 = emptyList()
        maxLineData3 = emptyList()
        minLineData3 = emptyList()

        viewModel.observeFeature(deviceId = deviceId, featureName = selectedFeature.value)
    }
}
