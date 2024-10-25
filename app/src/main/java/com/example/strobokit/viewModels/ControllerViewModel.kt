package com.example.strobokit.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.strobokit.composables.controllerAction
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.features.extended.navigation_control.NavigationControl
import com.st.blue_sdk.features.extended.navigation_control.request.MoveCommandDifferentialDriveSimpleMove
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
    private var lastSpeed = 0

    fun sendCommand(featureName: String, deviceId: String,action : controllerAction,angle : Int = 0, speed : Int = lastSpeed) {

        viewModelScope.launch {

            val feature = blueManager.nodeFeatures(deviceId).find { it.name == featureName } ?: return@launch
            if(feature is NavigationControl){
                    when(action){
                        controllerAction.Forward -> {
                            lastAction = 'F'
                            lastSpeed = speed
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                    feature = feature,
                                    action = 0x10u,
                                    direction = 'F'.code.toUByte(),
                                    speed = speed.toUByte(),
                                    angle = angle.toByte(),
                                    res = byteArrayOf(0)
                                ),//[linear motion , Directional motion , angle]
                                responseTimeout = 1L
                            )
                        }

                        controllerAction.Backward -> {
                            lastAction = 'B'
                            lastSpeed = speed
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                    feature = feature,
                                    action = 0x10u,
                                    direction = 'B'.code.toUByte(),
                                    speed = speed.toUByte(),
                                    angle = angle.toByte(),
                                    res = byteArrayOf(0)
//                                    byteArrayOf('B'.code.toByte(),'R'.code.toByte(),(angle/10).toByte())
                                ),
                                responseTimeout = 1L
                            )
                        }
                        controllerAction.Right -> {
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                    feature = feature,
                                    action = 0x10u,
                                    direction = 'R'.code.toUByte(),
                                    speed = speed.toUByte(),
                                    angle = angle.toByte(),
                                    res = byteArrayOf(0)
//                                    byteArrayOf(lastAction.code.toByte(),'R'.code.toByte(),(angle/10).toByte())
                                ),
                                responseTimeout = 1L
                            )
                        }
                        controllerAction.Left -> {
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                    feature = feature,
                                    action = 0x10u,
                                    direction = 'L'.code.toUByte(),
                                    speed = speed.toUByte(),
                                    angle = (360 - angle).toByte(),
                                    res = byteArrayOf(0)
//                                    byteArrayOf(lastAction.code.toByte(),'L'.code.toByte(),((360 - angle)/10).toByte())
                                ),
                                responseTimeout = 1L
                            )
                        }

                        controllerAction.Stop -> {
                            lastAction = 'S'
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                    feature = feature,
                                    action = 0x10u,
                                    direction = 'F'.code.toUByte(),
                                    speed = 0u,
                                    angle = 0,
                                    res = byteArrayOf(0)
                                ),
                                responseTimeout = 1L
                            )
                        }
                    }

            }
        }
    }
}