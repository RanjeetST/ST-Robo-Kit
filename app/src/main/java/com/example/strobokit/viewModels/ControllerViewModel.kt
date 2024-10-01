package com.example.strobokit.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.strobokit.composables.controllerAction
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.features.switchfeature.SwitchFeature
import com.st.blue_sdk.features.switchfeature.SwitchStatusType
import com.st.blue_sdk.features.switchfeature.request.SwitchOn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControllerViewModel @Inject constructor(
    private val blueManager: BlueManager
) : ViewModel(){

    companion object{
        private val TAG = ControllerViewModel::class.simpleName
    }

    private var lastAction = 'S'

    fun sendCommand(featureName: String, deviceId: String,currentValue : SwitchStatusType,action : controllerAction,angle : Int = 0) {

        viewModelScope.launch {

            val feature = blueManager.nodeFeatures(deviceId).find { it.name == featureName } ?: return@launch
            if(feature is SwitchFeature){
                Log.d(TAG,"$currentValue")
                 if(currentValue == SwitchStatusType.On){
                    when(action){
                        controllerAction.Forward -> {
                            lastAction = 'F'
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = SwitchOn(
                                    feature = feature,
                                    byteArrayOf('F'.code.toByte(),'R'.code.toByte(),(angle/10).toByte())
                                ),//[linear motion , Directional motion , angle]
                                responseTimeout = 1L
                            )
                        }

                        controllerAction.Backward -> {
                            lastAction = 'B'
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = SwitchOn(
                                    feature = feature,
                                    byteArrayOf('B'.code.toByte(),'R'.code.toByte(),(angle/10).toByte())
                                ),
                                responseTimeout = 1L
                            )
                        }
                        controllerAction.Right -> {
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = SwitchOn(
                                    feature = feature,
                                    byteArrayOf(lastAction.code.toByte(),'R'.code.toByte(),(angle/10).toByte())
                                ),
                                responseTimeout = 1L
                            )
                        }
                        controllerAction.Left -> {
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = SwitchOn(
                                    feature = feature,
                                    byteArrayOf(lastAction.code.toByte(),'L'.code.toByte(),((360 - angle)/10).toByte())
                                ),
                                responseTimeout = 1L
                            )
                        }

                        controllerAction.Stop -> {
                            lastAction = 'S'
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = SwitchOn(
                                    feature = feature,
                                    byteArrayOf('S'.code.toByte(),'0'.code.toByte(),(0).toByte())
                                ),
                                responseTimeout = 1L
                            )
                        }
                    }
                }
            }
        }
    }
}