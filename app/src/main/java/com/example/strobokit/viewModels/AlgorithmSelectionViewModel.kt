package com.example.strobokit.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.features.extended.navigation_control.NavigationControl
import com.st.blue_sdk.features.extended.navigation_control.request.SetNavigationMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlgorithmSelectionViewModel @Inject constructor(
    private val blueManager: BlueManager
) : ViewModel() {
    companion object{
        private val TAG = AlgorithmSelectionViewModel::class.simpleName
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