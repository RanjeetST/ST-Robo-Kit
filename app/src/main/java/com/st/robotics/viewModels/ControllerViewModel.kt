package com.st.robotics.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.features.extended.ext_configuration.ExtConfiguration
import com.st.blue_sdk.features.extended.ext_configuration.ExtendedFeatureResponse
import com.st.blue_sdk.features.extended.ext_configuration.request.ExtConfigCommands
import com.st.blue_sdk.features.extended.ext_configuration.request.ExtendedFeatureCommand
import com.st.blue_sdk.features.extended.robotics_movement.RoboticsMovement
import com.st.blue_sdk.features.extended.robotics_movement.request.MoveCommandDifferentialDriveSimpleMove
import com.st.blue_sdk.features.extended.robotics_movement.request.RobotDirection
import com.st.blue_sdk.features.extended.robotics_movement.request.RoboticsActionBits
import com.st.blue_sdk.features.extended.robotics_movement.request.SetNavigationMode
import com.st.blue_sdk.models.Node
import com.st.robotics.composables.ControllerAction
import com.st.robotics.viewModels.BleDeviceDetailViewModel.Commands
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

    val writeAction : List<RoboticsActionBits> = listOf(RoboticsActionBits.WRITE)

    //GIVES AN INSTANCE OF THE NODE
    fun bleDevice(deviceId: String): Flow<Node> =
        try {
            blueManager.getNodeStatus(nodeId = deviceId)
        } catch (ex: IllegalStateException) {
            flowOf()
        }

    //EXTRACT RSSI VALUE
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

    //DISABLE FEATURES
    fun disableFeatures(deviceId : String){
        rssiJob?.cancel()
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
                val command = ExtendedFeatureCommand(feature,ExtConfigCommands.buildConfigCommand(ExtConfigCommands.READ_VERSION_FW))
                val response = blueManager.writeFeatureCommand(
                    responseTimeout = 1250L,
                    nodeId = nodeId,
                    featureCommand = command
                )


                if(response is ExtendedFeatureResponse){
                    Log.d("Extended","Firmware version = ${response.response.versionFw}")
                }else{
                    Log.d("Extended","Firmware version : $response")
                }
            }
        }
    }

    //SEND THE CONTROLLER COMMANDS
    fun sendCommand(featureName: String, deviceId: String, action : ControllerAction, angle : Int = 0, speed : Int = 0) {

        viewModelScope.launch {

            val feature = blueManager.nodeFeatures(deviceId).find { it.name == featureName } ?: return@launch

            if(feature is RoboticsMovement){
                    when(action){
                        ControllerAction.Forward -> {
                            lastAction = 'F'
                            lastSpeed = speed
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                    feature = feature,
                                    action = writeAction,
                                    direction = RobotDirection.FORWARD,
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
                                    action = writeAction,
                                    direction = RobotDirection.BACKWARD,
                                    speed = lastSpeed.toUByte(),
                                    angle = angle.toByte(),
                                    res = byteArrayOf(0)
                                ),
                                responseTimeout = 1L
                            )
                        }
                        //TEMPORARY CHANGE IN CODE : R->P
                        ControllerAction.Right -> {
//                            val actionCode = if(lastSpeed == 0){
//                                'R'
//                            } else {
//                                'P'
//                            }

                            val actionCode = 'R'
                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                    feature = feature,
                                    action = writeAction,
                                    direction = RobotDirection.RIGHT,
                                    speed = lastSpeed.toUByte(),
                                    angle = angle.toByte(),
                                    res = byteArrayOf(0)
                                ),
                                responseTimeout = 1L
                            )

                        }

                        //TEMPORARY CHANGE IN CODE : L->Q
                        ControllerAction.Left -> {

//                            val actionCode = if(lastSpeed == 0){
//                                'L'
//                            } else {
//                                'Q'
//                            }

                            val actionCode = 'L'

                            blueManager.writeFeatureCommand(
                                nodeId = deviceId,
                                featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                    feature = feature,
                                    action = writeAction,
                                    direction = RobotDirection.LEFT,
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
                                    action = writeAction,
                                    direction = RobotDirection.STOP,
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

    private fun angleToPercentage(angle: Int): Int {
        return when (angle) {
            in 0..180 -> (angle / 180.0 * 100).toInt()
            in 181..360 -> -((360 - angle) / 180.0 * 100).toInt()
            else -> throw IllegalArgumentException("Angle must be between 0 and 360 degrees")
        }
    }

    fun sendCommand2(featureName: String, deviceId: String, action : ControllerAction, angle : Int = 0, speed : Int = 0) {

        viewModelScope.launch {

            val feature = blueManager.nodeFeatures(deviceId).find { it.name == featureName } ?: return@launch

            if(feature is RoboticsMovement){
                when(action){
                    ControllerAction.Forward -> {
                        lastAction = 'F'
                        lastSpeed = speed
                        blueManager.writeFeatureCommand(
                            nodeId = deviceId,
                            featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                feature = feature,
                                action = writeAction,
                                direction = RobotDirection.FORWARD,
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
                                action = writeAction,
                                direction = RobotDirection.BACKWARD,
                                speed = lastSpeed.toUByte(),
                                angle = angle.toByte(),
                                res = byteArrayOf(0)
                            ),
                            responseTimeout = 1L
                        )
                    }
                    //TEMPORARY CHANGE IN CODE : R->P
                    ControllerAction.Right -> {
//                            val actionCode = if(lastSpeed == 0){
//                                'R'
//                            } else {
//                                'P'
//                            }

                        val actionCode = 'T'

                        blueManager.writeFeatureCommand(
                            nodeId = deviceId,
                            featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                feature = feature,
                                action = writeAction,
                                direction = RobotDirection.LEFT,
                                speed = lastSpeed.toUByte(),
                                angle = angleToPercentage(angle).toByte(),
                                res = byteArrayOf(0)
                            ),
                            responseTimeout = 1L
                        )

                    }

                    //TEMPORARY CHANGE IN CODE : L->Q
                    ControllerAction.Left -> {

//                            val actionCode = if(lastSpeed == 0){
//                                'L'
//                            } else {
//                                'Q'
//                            }

                        val actionCode = 'T'

                        blueManager.writeFeatureCommand(
                            nodeId = deviceId,
                            featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                feature = feature,
                                action = writeAction,
                                direction = RobotDirection.RIGHT,
                                speed = lastSpeed.toUByte(),
                                angle = angleToPercentage(angle).toByte(),
                                res = byteArrayOf(0)
                            ),
                            responseTimeout = 1L
                        )
                    }

                    ControllerAction.Stop -> {
                        lastAction = 'S'
                        lastSpeed = 0
                        blueManager.writeFeatureCommand(
                            nodeId = deviceId,
                            featureCommand = MoveCommandDifferentialDriveSimpleMove(
                                feature = feature,
                                action = writeAction,
                                direction = RobotDirection.STOP,
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

    //TO SET THE NAVIGATION MODE OF THE CONNECTED NODE
    fun sendNavigationCommand(command : Commands, deviceId : String){

        viewModelScope.launch {
            getFwVersion(deviceId,ExtConfiguration.NAME)
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
            val feature = blueManager.nodeFeatures(deviceId).find { it.name == RoboticsMovement.NAME } ?: return@launch

            if(feature is RoboticsMovement){


                blueManager.writeFeatureCommand(
                    nodeId = deviceId,
                    featureCommand = SetNavigationMode(
                        feature = feature,
                        action = writeAction,
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