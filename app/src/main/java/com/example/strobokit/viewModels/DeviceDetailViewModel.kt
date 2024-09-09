package com.example.strobokit.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.features.Feature
import com.st.blue_sdk.models.Node
import com.st.blue_sdk.models.NodeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BleDeviceDetailViewModel @Inject constructor(
    private val blueManager: BlueManager
) : ViewModel() {

    val features = MutableStateFlow<List<Feature<*>>>(emptyList())

    fun bleDevice(deviceId: String): Flow<Node> =
        try {
            blueManager.getNodeStatus(nodeId = deviceId)
        } catch (ex: IllegalStateException) {
            flowOf()
        }

    fun getFeatures(deviceId: String) {
        features.update { blueManager.nodeFeatures(nodeId = deviceId) }
    }

    fun disconnect(deviceId: String) {
        features.update { emptyList() }
        viewModelScope.launch {
            blueManager.disconnect(nodeId = deviceId)
        }
    }
}