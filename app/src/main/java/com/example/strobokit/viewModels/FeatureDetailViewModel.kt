package com.example.strobokit.viewModels


import android.util.Log
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
    private var currentSwitchValue: SwitchStatusType = SwitchStatusType.Off
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
                                Log.d(TAG, "calibration status ${it.status}")
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

//    fun sendExtendedCommand(featureName: String, deviceId: String,currentValue: SwitchStatusType) {
//
//        viewModelScope.launch {
//
//            val feature = blueManager.nodeFeatures(deviceId).find { it.name == featureName } ?: return@launch
//
//            if(feature is SwitchFeature){
//                if(currentValue == SwitchStatusType.Off)
//                {
//                    blueManager.writeFeatureCommand(
//                        deviceId,
//                        SwitchOn(feature = feature)
//                    )
//                }else if(currentValue == SwitchStatusType.On){
//                    blueManager.writeFeatureCommand(
//                        deviceId,
//                        SwitchOff(feature = feature)
//                    )
//                }
//            }
//        }
//    }

    fun sendExtendedCommand(featureName: String, deviceId: String,flag: Boolean) {

        viewModelScope.launch {

            val feature = blueManager.nodeFeatures(deviceId).find { it.name == featureName } ?: return@launch

            if(feature is SwitchFeature){
                if(flag)
                {
                    blueManager.writeFeatureCommand(
                        deviceId,
                        SwitchOn(feature = feature)
                    )
                }else{
                    blueManager.writeFeatureCommand(
                        deviceId,
                        SwitchOff(feature = feature)
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