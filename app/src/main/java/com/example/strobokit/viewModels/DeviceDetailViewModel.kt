package com.example.strobokit.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.features.Feature
import com.st.blue_sdk.features.battery.Battery
import com.st.blue_sdk.features.battery.BatteryInfo
import com.st.blue_sdk.features.extended.navigation_control.NavigationControl
import com.st.blue_sdk.features.extended.navigation_control.request.SetNavigationMode
import com.st.blue_sdk.models.Node
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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

    private var featureJob: Job? = null
    private var rssiJob: Job? = null

    fun bleDevice(deviceId: String): Flow<Node> =
        try {
            blueManager.getNodeStatus(nodeId = deviceId)
        } catch (ex: IllegalStateException) {
            flowOf()
        }

    fun getFeatures(deviceId: String) {
//        features.update { blueManager.nodeFeatures(nodeId = deviceId) }

        if(batteryFeature == null){
            blueManager.nodeFeatures(nodeId = deviceId).find{
                Battery.NAME == it.name
            }?.let { f ->
                batteryFeature = f
            }
        }

        batteryFeature?.let{
            featureJob = viewModelScope.launch{
                blueManager.getFeatureUpdates(nodeId = deviceId, listOf(it)).collect {
                    val data = it.data
                    if (data is BatteryInfo) {
                        _batteryData.emit(data)
                    }
                }
            }
        }

        rssiJob = viewModelScope.launch {
            try {
                while(isActive){
                    blueManager.getRssi(deviceId)
                    delay(1000)
                }
            }catch (
                _: IllegalStateException
            ){ }

        }
    }

     fun disableFeatures(deviceId : String){
         batteryFeature?.let {
             viewModelScope.launch{
                 blueManager.disableFeatures(deviceId, listOf(it))
             }
         }
         rssiJob?.cancel()
         featureJob?.cancel()
    }


    fun disconnect(deviceId: String) {
        features.update { emptyList() }

        viewModelScope.launch {
            blueManager.disconnect(nodeId = deviceId)
        }
    }

    enum class Commands{
        FOLLOW_ME,
        FREE_NAVIGATION,
        REMOTE_CONTROL
    }

    fun sendCommand(command : Commands,deviceId : String){

        viewModelScope.launch {
            val commandId = when(command){
                Commands.REMOTE_CONTROL -> {
                    0x01u
                }
                Commands.FREE_NAVIGATION ->{
                    0x02u
                }
                Commands.FOLLOW_ME -> {
                    0x03u
                }
            }
//            Log.d(TAG,"Feature not found command id = ${commandId.toUByte()}")
            val feature = blueManager.nodeFeatures(deviceId).find { it.name == "Navigation Control" } ?: return@launch

            if(feature is NavigationControl){
                Log.d("navigation","${feature.name} + ${commandId.toUByte()}")
                blueManager.writeFeatureCommand(
                    nodeId = deviceId,
                    featureCommand = SetNavigationMode(
                        feature = feature,
                        action = 16u,
                        navigationMode = commandId.toUByte(),
                        armed = 0u,
                        res = 0L
                    ),
                    responseTimeout = 1L
                )
            }
        }
    }
}