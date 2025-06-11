package com.st.robotics.viewModels

import android.net.Uri
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.common.Status
import com.st.blue_sdk.models.Boards
import com.st.blue_sdk.models.Node
import com.st.blue_sdk.models.NodeState
import com.st.robotics.models.QrCode
import com.st.robotics.utilities.QrCodeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.contains

@HiltViewModel
class BleDeviceListViewModel @Inject constructor(
    private val blueManager: BlueManager,
    private val qrCodeService: QrCodeService
) : ViewModel() {
    companion object {
        private val TAG = BleDeviceDetailViewModel::class.simpleName
        private const val MAX_RETRY_CONNECTION = 3
        val ROBOTICS_SUPPORTED_BOARDS = listOf(
            Boards.Model.STEVALROBKIT
        )
    }

    val scanBleDevices = MutableStateFlow<List<Node>>(emptyList())
    val isLoading = MutableStateFlow(false)
    private var filter: Boolean = false

    private var scanPeripheralJob: Job? = null

    var isRefreshing by mutableStateOf(false)
        private set

    private val _connectionState = MutableStateFlow(NodeState.Disconnected)
    val connectionState: StateFlow<NodeState> = _connectionState.asStateFlow()

    private val _pendingNavigationAddress = MutableStateFlow<String?>(null)
    val pendingNavigationAddress: StateFlow<String?> = _pendingNavigationAddress.asStateFlow()

    private var connectionJob: Job? = null
    private var qrScanJob: Job? = null

    private val _dialogMessage = MutableStateFlow<String?>(null)
    val dialogMessage: StateFlow<String?> = _dialogMessage.asStateFlow()

    fun showDialog(message: String) {
        _dialogMessage.value = message
    }

    fun dismissDialog() {
        _dialogMessage.value = null
    }

    //HANDLES RESCAN ON REFRESH PULL DOWN
    fun onRefresh() {
        viewModelScope.launch {
            isRefreshing = true
            startScan(filter = filter)
            isRefreshing = false
        }
    }

    //FUNCTION TO START SCANNING
    fun startScan(filter : Boolean = this.filter) {
        this.filter = filter
//        connectionJob?.cancel()
        scanPeripheralJob?.cancel()
        scanPeripheralJob = viewModelScope.launch {
            blueManager.scanNodes().map {resource ->
                isLoading.tryEmit(value = resource.status == Status.LOADING)

                resource.data ?: emptyList()
            }.collect {nodes->
                val filteredNodes = nodes.filter { node ->
                    ROBOTICS_SUPPORTED_BOARDS.contains(node.boardType)
                }
//                nodes.map { node ->
//                    Log.d("Board",node.boardType.toString())
//                    Log.d("Board",node.advertiseInfo?.getAddress().toString())
//                }
                if(filter){
                    scanBleDevices.tryEmit(value = filteredNodes)
                }else{
                    scanBleDevices.tryEmit(value = nodes)
                }

            }
        }
    }

    fun scanQrCode(onNodeFound: (String?) -> Unit) {
        qrScanJob?.cancel()

        qrScanJob = viewModelScope.launch {
            val value = decodeQrCode(qrCodeService.scan())
            val uri: Uri = Uri.parse(value)

            val address = uri.getQueryParameter("address")
                ?: uri.getQueryParameter("mac")
                ?: uri.getQueryParameter("url")

            if (address != null) {
                // Get current list of nodes instead of collecting continuously
                val currentNodes = scanBleDevices.value
                val matchingNode = currentNodes.find { node -> node.device.address == address }

                if (matchingNode != null) {
                    onNodeFound(address)
                    Log.d(TAG, "Node found via QR code. Address: $address")
                } else {
                    showDialog("No matching node found for address: $address")
                }
            } else {
                showDialog("Invalid QR code: Address not found")
            }
        }
    }

    fun cancelQrScan() {
        qrScanJob?.cancel()
        qrScanJob = null
    }

    override fun onCleared() {
        super.onCleared()
        scanPeripheralJob?.cancel()
        connectionJob?.cancel()
        qrScanJob?.cancel()
    }

    fun clearPendingNavigation() {
        _pendingNavigationAddress.value = null
    }

    private suspend fun decodeQrCode(qrCode : String?) : String{
        if (qrCode == null) return ""

        Log.e(TAG, "qrCode = $qrCode")

        return qrCode
    }

    //TO CONNECT WITH THE NODE
    fun connect(deviceId: String, maxConnectionRetries: Int = MAX_RETRY_CONNECTION,onNodeReady: (()-> Unit)? = null) {
        connectionJob?.cancel()
        connectionJob = viewModelScope.launch {
            val isMainThread = Looper.myLooper() == Looper.getMainLooper()
            Log.d("BleDeviceListV2", " [MainThread: $isMainThread] [Thread: ${Thread.currentThread().name}]")
            var retryCount = 0
            //var callback = onNodeReady
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
                    Log.d(TAG, "Node is ready. Connection state: $currentNodeState")
                    //delay(500)
                    //callback?.invoke()
                    //callback = null
                }
            }
        }
    }
}