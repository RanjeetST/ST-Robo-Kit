package com.example.strobokit.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.features.Feature
import com.st.blue_sdk.features.battery.Battery
import com.st.blue_sdk.features.battery.BatteryInfo
import com.st.blue_sdk.models.Node
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
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

    private val _hasBatteryFeatureFlag = MutableLiveData(false)
    val hasBatteryFeatureFlag: LiveData<Boolean>
        get() = _hasBatteryFeatureFlag

    fun bleDevice(deviceId: String): Flow<Node> =
        try {
            blueManager.getNodeStatus(nodeId = deviceId)
        } catch (ex: IllegalStateException) {
            flowOf()
        }

    fun getFeatures(deviceId: String) {
        features.update { blueManager.nodeFeatures(nodeId = deviceId) }

        if(batteryFeature == null){
            blueManager.nodeFeatures(nodeId = deviceId).find{
                Battery.NAME == it.name
            }?.let { f ->
                batteryFeature = f
                _hasBatteryFeatureFlag.value = true
            }
        }

        batteryFeature?.let{
            viewModelScope.launch{
                blueManager.getFeatureUpdates(nodeId = deviceId, listOf(it)).collect{
                    val data = it.data
                    if(data is BatteryInfo){
                        _batteryData.emit(data)
//                        Log.d("BatteryData",data.toString())
//                        Level = 93.0 %
//                        Status = Discharging
//                        Voltage = 4.085 V
//                        Current = 0.0 mA
                    }
                }
            }
        }
    }

    fun disconnect(deviceId: String) {
        features.update { emptyList() }
        viewModelScope.launch {
            blueManager.disconnect(nodeId = deviceId)
        }
    }
}