package com.st.robotics.viewModels


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.features.CalibrationStatus
import com.st.blue_sdk.features.FeatureUpdate
import com.st.blue_sdk.features.compass.Compass
import com.st.blue_sdk.features.switchfeature.SwitchFeature
import com.st.blue_sdk.features.switchfeature.SwitchStatusType
import com.st.blue_sdk.features.switchfeature.request.SwitchOff
import com.st.blue_sdk.features.switchfeature.request.SwitchOn
import com.st.blue_sdk.services.calibration.CalibrationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeatureDetailViewModel @Inject constructor(
    private val blueManager: BlueManager,
    private val calibrationService: CalibrationService
) : ViewModel() {

    companion object {
        private val TAG = FeatureDetailViewModel::class.simpleName
    }

    val featureUpdates: State<FeatureUpdate<*>?>
        get() = _featureUpdates

    private val _featureUpdates = mutableStateOf<FeatureUpdate<*>?>(null)

    fun startCalibration(deviceId: String, featureName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            blueManager.nodeFeatures(deviceId)
                .find { it.name == featureName && it.name == Compass.NAME }
                ?.let { feature ->
                    val isCalibrated =
                        calibrationService.startCalibration(nodeId = deviceId, feature = feature)
                    if (!isCalibrated.status) {
                        blueManager.getConfigControlUpdates(nodeId = deviceId).collect {
                            if (it is CalibrationStatus) {
                            }
                        }
                    }
                }
        }
    }

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

    fun sendCommand(featureName: String, deviceId: String,currentValue : SwitchStatusType) {

        viewModelScope.launch {

            val feature = blueManager.nodeFeatures(deviceId).find { it.name == featureName } ?: return@launch
            if(feature is SwitchFeature){
                if(currentValue == SwitchStatusType.Off)
                {
                    blueManager.writeFeatureCommand(
                        nodeId = deviceId,
                        featureCommand = SwitchOn(feature, byteArrayOf(2)),
                        responseTimeout = 1L
                    )
                }else if(currentValue == SwitchStatusType.On){
                    blueManager.writeFeatureCommand(
                        nodeId = deviceId,
                        featureCommand = SwitchOff(feature = feature,byteArrayOf(1)),
                        responseTimeout = 1L
                    )
                }
            }
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