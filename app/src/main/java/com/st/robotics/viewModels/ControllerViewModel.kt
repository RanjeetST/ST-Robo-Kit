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
import com.st.blue_sdk.features.extended.robotics_movement.RoboticsMovement
import com.st.blue_sdk.features.extended.robotics_movement.request.MoveCommandDifferentialDriveArticulatingMove
import com.st.blue_sdk.features.extended.robotics_movement.request.MoveCommandDifferentialDriveSimpleMove
import com.st.blue_sdk.features.extended.robotics_movement.request.NavigationMode
import com.st.blue_sdk.features.extended.robotics_movement.request.RobotDirection
import com.st.blue_sdk.features.extended.robotics_movement.request.RoboticsActionBits
import com.st.blue_sdk.features.extended.robotics_movement.request.SetNavigationMode
import com.st.blue_sdk.models.Node
import com.st.robotics.composables.ControllerAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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

    private var batteryFeature : Feature<*> ? = null

    private var lastAction = 'S'
    private var lastSpeed = 0
    private var lastAngle = 0

    private var rssiJob: Job? = null
    private var featureJob: Job? = null

    private val _batteryData = MutableSharedFlow<BatteryInfo>()
    val batteryData: Flow<BatteryInfo>
        get() = _batteryData

    val firmwareVersion: State<String?>
        get() = _firmwareVersion
    private val _firmwareVersion = mutableStateOf<String?>(null)

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

        if(batteryFeature == null){
            blueManager.nodeFeatures(nodeId = deviceId).find{
                Battery.NAME == it.name
            }?.let { f ->
                batteryFeature = f
            }
        }

        batteryFeature?.let{

            featureJob = viewModelScope.launch{
                if(batteryFeature != null){
                    blueManager.enableFeatures(
                        nodeId = deviceId,
                        features = listOf(it)
                    )
                }
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
                    }else{
                        Log.d("Extended","Firmware version : $response")
                    }

                    MAX_TRY -= 1
                    delay(100)
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

    fun sendCommand2(featureName: String, deviceId: String, rotationAngle : Int = lastAngle,linearAngle: Int = 0, speed : Int = lastSpeed) {

        viewModelScope.launch {

            val feature = blueManager.nodeFeatures(deviceId).find { it.name == featureName } ?: return@launch

            if(feature is RoboticsMovement){
                lastSpeed = speed
                lastAngle = rotationAngle

                // Check if this is a curve command (both angle and speed provided)
                var isCurveCommand = rotationAngle != 0 && speed != 0

               // Log.d("TAG","Angle = ${angleToPercentage(lastAngle)} , Speed = $lastSpeed,linearAngle = $linearAngle")

                // Send the initial command
                blueManager.writeFeatureCommand(
                    nodeId = deviceId,
                    featureCommand = MoveCommandDifferentialDriveArticulatingMove(
                        feature = feature,
                        action = writeAction,
                        speed = lastSpeed.toByte(),
                        rotationAngle = angleToPercentage(lastAngle).toByte(),
                        linearAngle = linearAngle.toByte(),
                        res = byteArrayOf(0)
                    ),
                    responseTimeout = 1L
                )

                // If it's a curve command, resend ONLY the speed command after a delay
                if (isCurveCommand) {
                    // Add delay to allow curve to complete
                    isCurveCommand = false

                    delay(2000) // Adjust this delay based on your rover's curve completion time

                    //Log.d("TAG", "Resending speed command after curve completion, $lastSpeed")
                    // Send ONLY speed command (no angle) to continue straight movement
                    sendCommand2(
                        featureName = featureName,
                        deviceId = deviceId,
                        rotationAngle = 0, // No rotation for straight movement
                        linearAngle = linearAngle,
                        speed = lastSpeed
                    )
                }
            }
        }
    }

    //TO SET THE NAVIGATION MODE OF THE CONNECTED NODE
    fun sendNavigationCommand(command : NavigationMode, deviceId : String){

        val armed = if(command == NavigationMode.LOCK){
            0u
        }else{
            1u
        }
        viewModelScope.launch {
            val feature = blueManager.nodeFeatures(deviceId).find { it.name == RoboticsMovement.NAME } ?: return@launch

            if(feature is RoboticsMovement){

                Log.d("lock22","Sent stop command")
                blueManager.writeFeatureCommand(
                    nodeId = deviceId,
                    featureCommand = SetNavigationMode(
                        feature = feature,
                        action = writeAction,
                        navigationMode = command,
                        armed = armed.toUByte(),
                        res = 0L
                    ),
                    responseTimeout = 1L
                )
            }
        }
    }
}