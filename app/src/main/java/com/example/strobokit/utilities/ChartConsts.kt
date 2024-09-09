package com.example.strobokit.utilities

import android.util.Log
import androidx.compose.ui.geometry.Offset
import com.example.strobokit.viewModels.PlotEntry
import com.st.blue_sdk.features.Feature
import com.st.blue_sdk.features.FeatureUpdate
import com.st.blue_sdk.features.acceleration.Acceleration
import com.st.blue_sdk.features.acceleration.AccelerationInfo
import com.st.blue_sdk.features.gyroscope.Gyroscope
import com.st.blue_sdk.features.gyroscope.GyroscopeInfo
import com.st.blue_sdk.features.magnetometer.Magnetometer
import com.st.blue_sdk.features.magnetometer.MagnetometerInfo

//add features you need to plot
val PLOTTABLE_FEATURE = listOf(
    Acceleration.NAME,
    Gyroscope.NAME,
    Magnetometer.NAME,
)

internal fun Feature<*>.fieldDesc(): Map<String,String> =
    when (this){
        is Acceleration -> mapOf("X" to "mg","Y" to "mg","Z" to "mg")
        is Gyroscope -> mapOf("X" to "dps", "Y" to "dps", "Z" to "dps")
        is Magnetometer -> mapOf("X" to "mGa", "Y" to "mGa", "Z" to "mGa")

        else -> mapOf("Events" to "")
    }

internal fun FeatureUpdate<*>.toPlotEntry(feature: Feature<*>, xOffset: Long): PlotEntry? =
    when(val data = this.data){
        is AccelerationInfo ->
            if(feature is Acceleration){
                Log.d("plot","TS:$timeStamp X:${data.x.value} Y:${data.y.value} Z:${data.z.value}")
                PlotEntry(
                    x = notificationTime.time - xOffset,
                    listOf(data.x.value,data.y.value,data.z.value).toFloatArray()
                )
            }else {
                null
            }

        is GyroscopeInfo ->
            if (feature is Gyroscope){
                PlotEntry(
                    x = notificationTime.time - xOffset,
                    listOf(data.x.value,data.y.value,data.z.value).toFloatArray()
                )
            }else{
                null
            }

        is MagnetometerInfo ->
            if (feature is Magnetometer){
                PlotEntry(
                    x = notificationTime.time - xOffset,
                    listOf(data.x.value,data.y.value,data.z.value).toFloatArray()
                )
            }else{
                null
            }

        else -> null
    }

internal fun FeatureUpdate<*>.toPlotDescription(feature : Feature<*>) : String  ? =
    when(val data = this.data){
        is AccelerationInfo ->
            if(feature is Acceleration){
                Log.d("plot","TS:$timeStamp X:${data.x.value} Y:${data.y.value} Z:${data.z.value}")
                "TS:$timeStamp X:${data.x.value} Y:${data.y.value} Z:${data.z.value}"
            }else{
                null
            }

        is GyroscopeInfo ->
            if(feature is Gyroscope){
                "TS:$timeStamp X: ${data.x.value} Y:${data.y.value} Z: ${data.z.value}"
            }else{
                null
            }

        is MagnetometerInfo ->
            if (feature is Magnetometer){
                "TS:$timeStamp X: ${data.x.value} Y:${data.y.value} Z: ${data.z.value}"
            }else{
                null
            }


        else -> null
    }