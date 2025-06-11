package com.st.robotics.viewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.features.Feature
import com.st.blue_sdk.features.battery.Battery
import com.st.blue_sdk.features.battery.BatteryInfo
import com.st.blue_sdk.features.extended.ext_configuration.ExtConfiguration
import com.st.blue_sdk.features.extended.ext_configuration.ExtendedFeatureResponse
import com.st.blue_sdk.features.extended.ext_configuration.request.ExtConfigCommands
import com.st.blue_sdk.features.extended.ext_configuration.request.ExtendedFeatureCommand
import com.st.blue_sdk.models.ConnectionStatus
import com.st.blue_sdk.models.Node
import com.st.robotics.models.FirmwareDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BleDeviceDetailViewModel @Inject constructor(
    private val blueManager: BlueManager
) : ViewModel() {

    val features = MutableStateFlow<List<Feature<*>>>(emptyList())

    private var batteryFeature : Feature<*> ? = null

    private val _batteryData = MutableSharedFlow<BatteryInfo>()
    val batteryData: Flow<BatteryInfo>
        get() = _batteryData
    private val _nodeStatus = MutableStateFlow<ConnectionStatus?>(null)
    val nodeStatus: StateFlow<ConnectionStatus?>
        get() = _nodeStatus

    private var featureJob: Job? = null
    private var rssiJob: Job? = null

    val firmwareVersion: State<String?>
        get() = _firmwareVersion
    private val _firmwareVersion = mutableStateOf<String?>(null)

    //GIVES AN INSTANCE OF THE NODE
    fun bleDevice(deviceId: String): Flow<Node> =
        try {
            blueManager.getNodeStatus(nodeId = deviceId)
        } catch (ex: IllegalStateException) {
            flowOf()
        }

    //GIVES ALL THE FEATURES ADVERTISED BY THE NODE
    fun getFeatures(deviceId: String) {
        // Cancel existing jobs before starting new ones
        stopMonitoring()

        // Find and cache battery feature if not already found
        if (batteryFeature == null) {
            batteryFeature = blueManager.nodeFeatures(nodeId = deviceId)
                .find { Battery.NAME == it.name }
        }

        // Start battery monitoring
        batteryFeature?.let { feature ->
            featureJob = viewModelScope.launch {
                try {
                    // Enable the feature
                    blueManager.enableFeatures(
                        nodeId = deviceId,
                        features = listOf(feature)
                    )

                    // Collect battery updates
                    blueManager.getFeatureUpdates(nodeId = deviceId, listOf(feature))
                        .collect { update ->
                            val data = update.data
                            if (data is BatteryInfo) {
                                _batteryData.emit(data)
                            }
                        }
                } catch (e: Exception) {
                    // Handle errors (device disconnected, feature unavailable, etc.)
                    Log.e("BatteryMonitor", "Error monitoring battery: ${e.message}")
                }
            }
        }

        // Start RSSI monitoring
        startRssiMonitoring(deviceId)
    }

    private fun startRssiMonitoring(deviceId: String) {
        rssiJob = viewModelScope.launch {
            try {
                while (isActive) {
                    blueManager.getRssi(deviceId)
                    delay(1000)
                }
            } catch (e: Exception) {
                // Log but don't crash - RSSI monitoring is non-critical
                Log.w("RssiMonitor", "RSSI monitoring stopped: ${e.message}")
            }
        }
    }

    fun getFwVersion(nodeId: String,featureName: String){
        viewModelScope.launch {
            val feature =
                blueManager.nodeFeatures(nodeId).find { it.name == featureName } ?: return@launch
            val features = listOf(feature)
            blueManager.enableFeatures(
                nodeId = nodeId,
                features = features
            )
            if(feature is ExtConfiguration){
                var MAX_TRY = 3

                while (_firmwareVersion.value == null && MAX_TRY > 0){
                    val command = ExtendedFeatureCommand(feature,ExtConfigCommands.buildConfigCommand(ExtConfigCommands.READ_VERSION_FW))
                    val response = blueManager.writeFeatureCommand(
                        responseTimeout = 1250L,
                        nodeId = nodeId,
                        featureCommand = command
                    )

                    if(response is ExtendedFeatureResponse){

                        Log.d("Extended","Firmware version = ${response.response.versionFw}")
                        _firmwareVersion.value = response.response.versionFw
                        FirmwareDetails.firmwareVersion = response.response.versionFw
                    }else{
                        Log.d("Extended","Firmware version : $response")
                    }

                    MAX_TRY -= 1
                    delay(100)
                }

            }
        }
    }

    fun stopMonitoring() {
        featureJob?.cancel()
        rssiJob?.cancel()
        featureJob = null
        rssiJob = null
    }

    override fun onCleared() {
        super.onCleared()
        //stopMonitoring()
    }

    //TO DISABLE THE FEATURES WHEN DISCONNECTED
     fun disableFeatures(deviceId : String){

         batteryFeature?.let {
             viewModelScope.launch{
                 blueManager.disableFeatures(deviceId, listOf(it))
             }
         }
         rssiJob?.cancel()
         featureJob?.cancel()
    }

    //TO DISCONNECT FROM THE NODE
    fun disconnect(deviceId: String) {
        features.update { emptyList() }

        viewModelScope.launch {
            blueManager.disconnect(nodeId = deviceId)
        }
    }
}