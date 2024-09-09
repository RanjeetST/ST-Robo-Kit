package com.example.strobokit.viewModels

import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.strobokit.utilities.PLOTTABLE_FEATURE
import com.example.strobokit.utilities.toPlotDescription
import com.example.strobokit.utilities.toPlotEntry
import com.example.strobokit.viewModels.FeatureDetailViewModel.Companion
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.features.CalibrationStatus
import com.st.blue_sdk.features.Feature
import com.st.blue_sdk.features.FeatureUpdate
import com.st.blue_sdk.features.acceleration.Acceleration
import com.st.blue_sdk.features.compass.Compass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlotViewModel @Inject constructor(
    private val blueManager: BlueManager
) : ViewModel() {
    companion object {
        private val TAG = FeatureDetailViewModel::class.simpleName
    }

    val featureUpdates: State<FeatureUpdate<*>?>
        get() = _featureUpdates

    private val _featureUpdates = mutableStateOf<FeatureUpdate<*>?>(null)

    private var observeFeatureJob: Job? = null

    fun observeFeature(featureName: String, deviceId: String) {
        observeFeatureJob?.cancel()

        blueManager.nodeFeatures(deviceId).find { it.name == featureName }?.let { feature ->
            observeFeatureJob =
                blueManager.getFeatureUpdates(nodeId = deviceId, features = listOf(feature))
                    .flowOn(Dispatchers.IO)
                    .onEach {
                        _featureUpdates.value = it
                    }.launchIn(viewModelScope)
        }
    }

    fun disconnectFeature(deviceId: String, featureName: String) {
        observeFeatureJob?.cancel()
        _featureUpdates.value = null
        viewModelScope.launch {
            val features = blueManager.nodeFeatures(deviceId).filter { it.name == featureName }
            blueManager.disableFeatures(
                nodeId = deviceId,
                features = features
            )
        }
    }
}


// to initiate plot
data class PlotEntry(
    val x: Long, val y: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlotEntry

        if (x != other.x) return false
        if (!y.contentEquals(other.y)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.contentHashCode()
        return result
    }
}
