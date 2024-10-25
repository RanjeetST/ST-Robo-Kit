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

//            val feature = blueManager.nodeFeatures(deviceId).find { it.name == featureName } ?: return@launch

                    when(action){
                        controllerAction.Forward -> {
                            lastAction = 'F'
                            blueManager.writeDebugMessage(
                                nodeId = deviceId,
                                msg = lastAction.toString()
                            )
                        }
                        controllerAction.Backward -> {
                            lastAction = 'B'
                            lastSpeed = speed
                            blueManager.writeDebugMessage(
                                nodeId = deviceId,
                                msg = lastAction.toString()
                            )
                        }
                        controllerAction.Right -> {
                            blueManager.writeDebugMessage(
                                nodeId = deviceId,
                                msg = 'R'.toString()
                            )
                        }
                        controllerAction.Left -> {
                            blueManager.writeDebugMessage(
                                nodeId = deviceId,
                                msg = 'L'.toString()
                            )
                        }

                        controllerAction.Stop -> {
                            lastAction = 'S'
                            blueManager.writeDebugMessage(
                                nodeId = deviceId,
                                msg = lastAction.toString()
                            )
                        }
                    }
        }
    }
}