package com.st.robotics.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.robotics.composables.ControllerAction
import com.st.robotics.viewModels.BleDeviceDetailViewModel.Commands
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.features.extended.navigation_control.NavigationControl
import com.st.blue_sdk.features.extended.navigation_control.request.MoveCommandDifferentialDriveSimpleMove
import com.st.blue_sdk.features.extended.navigation_control.request.SetNavigationMode
import com.st.blue_sdk.models.Node
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.isActive
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


    private var rssiJob: Job? = null

    fun bleDevice(deviceId: String): Flow<Node> =
        try {
            blueManager.getNodeStatus(nodeId = deviceId)
        } catch (ex: IllegalStateException) {
            flowOf()
        }

    fun getRssi(deviceId: String) {

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
        rssiJob?.cancel()
    }

    fun sendCommand(featureName: String, deviceId: String, action : ControllerAction, angle : Int = 0, speed : Int = 0) {

        viewModelScope.launch {

            val feature = blueManager.nodeFeatures(deviceId).find { it.name == featureName } ?: return@launch

            if(feature is NavigationControl){
                    when(action){
                        ControllerAction.Forward -> {
                            lastAction = 'F'
                            lastSpeed = speed
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                    feature = feature,
                                    action = 0x10u,
                                    direction = 'F'.code.toUByte(),
                                    speed = lastSpeed.toUByte(),
                                    angle = angle.toByte(),
                                    res = byteArrayOf(0)
                                ),
                                responseTimeout = 1L
                            )
                        }

                        ControllerAction.Backward -> {
                            lastSpeed = speed
                            lastAction = 'B'
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                    feature = feature,
                                    action = 0x10u,
                                    direction = 'B'.code.toUByte(),
                                    speed = lastSpeed.toUByte(),
                                    angle = angle.toByte(),
                                    res = byteArrayOf(0)
                                ),
                                responseTimeout = 1L
                            )
                        }
                        ControllerAction.Right -> {
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                    feature = feature,
                                    action = 0x10u,
                                    direction = 'R'.code.toUByte(),
                                    speed = lastSpeed.toUByte(),
                                    angle = angle.toByte(),
                                    res = byteArrayOf(0)
                                ),
                                responseTimeout = 1L
                            )

                        }
                        ControllerAction.Left -> {
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                    feature = feature,
                                    action = 0x10u,
                                    direction = 'L'.code.toUByte(),
                                    speed = lastSpeed.toUByte(),
                                    angle = (360 - angle).toByte(),
                                    res = byteArrayOf(0)
                                ),
                                responseTimeout = 1L
                            )
                        }

                        ControllerAction.Stop -> {
                            lastAction = 'S'
                            lastSpeed = speed
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                    feature = feature,
                                    action = 0x10u,
                                    direction = 'S'.code.toUByte(),
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

    fun sendNavigationCommand(command : Commands, deviceId : String){

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
            val feature = blueManager.nodeFeatures(deviceId).find { it.name == NavigationControl.NAME } ?: return@launch

            if(feature is NavigationControl){
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