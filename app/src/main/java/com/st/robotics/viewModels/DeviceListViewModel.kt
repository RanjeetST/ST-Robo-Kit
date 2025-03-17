package com.st.robotics.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.common.Status
import com.st.blue_sdk.models.Node
import com.st.blue_sdk.models.NodeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BleDeviceListViewModel @Inject constructor(
    private val blueManager: BlueManager
) : ViewModel() {
    companion object {
        private val TAG = BleDeviceDetailViewModel::class.simpleName
        private const val MAX_RETRY_CONNECTION = 3
    }

    val scanBleDevices = MutableStateFlow<List<Node>>(emptyList())
    val isLoading = MutableStateFlow(false)

    private var scanPeripheralJob: Job? = null

    var isRefreshing by mutableStateOf(false)
        private set

    private val _connectionState = MutableStateFlow(NodeState.Disconnected)
    val connectionState: StateFlow<NodeState> = _connectionState.asStateFlow()

    private var connectionJob: Job? = null

    //HANDLES RESCAN ON REFRESH PULL DOWN
    fun onRefresh() {
        viewModelScope.launch {
            isRefreshing = true
            startScan()
            isRefreshing = false
        }
    }

    //FUNCTION TO START SCANNING
    fun startScan() {
//        connectionJob?.cancel()
        scanPeripheralJob?.cancel()
        scanPeripheralJob = viewModelScope.launch {
            blueManager.scanNodes().map {resource ->
                isLoading.tryEmit(value = resource.status == Status.LOADING)

                resource.data ?: emptyList()
            }.collect {nodes->
                scanBleDevices.tryEmit(value = nodes)
            }
        }
    }

    //TO CONNECT WITH THE NODE
    fun connect(deviceId: String, maxConnectionRetries: Int = MAX_RETRY_CONNECTION,onNodeReady: (()-> Unit)? = null) {
        connectionJob?.cancel()
        connectionJob = viewModelScope.launch {
            var retryCount = 0
            var callback = onNodeReady
            connectionJob?.cancel()
            blueManager.connectToNode(deviceId).collect {

                val previousNodeState = it.connectionStatus.prev
                val currentNodeState = it.connectionStatus.current
                _connectionState.value = currentNodeState

                if (previousNodeState == NodeState.Connecting &&
                    currentNodeState == NodeState.Disconnected
                ) {
                    retryCount += 1

                    if (retryCount > maxConnectionRetries) {
                        return@collect
                    }

                    blueManager.connectToNode(deviceId)
                }

                if (currentNodeState == NodeState.Ready) {

                    delay(500)
                    callback?.invoke()
                    callback = null
                }
            }
        }
    }
}