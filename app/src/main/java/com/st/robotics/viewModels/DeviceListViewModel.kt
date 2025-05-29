package com.st.robotics.viewModels

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
    private val blueManager: BlueManager
) : ViewModel() {
    companion object {
        private val TAG = BleDeviceDetailViewModel::class.simpleName
        private const val MAX_RETRY_CONNECTION = 3
        val ROBOTICS_SUPPORTED_BOARDS = listOf(
            Boards.Model.STEVALROBKIT,
            Boards.Model.SENSOR_TILE_BOX_PRO,
            Boards.Model.SENSOR_TILE_BOX,
//            Boards.Model.GENERIC
        )
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
                val filteredNodes = nodes.filter { node ->
                    ROBOTICS_SUPPORTED_BOARDS.contains(node.boardType)
                }
                nodes.map { node ->
                    Log.d("Board",node.boardType.toString())
                    Log.d("Board",node.advertiseInfo?.getAddress().toString())
                }
                scanBleDevices.tryEmit(value = filteredNodes)
            }
        }
    }

    fun scanQrCode(){
        viewModelScope.launch {

            val value = decodeQrCode(qrCodeService.scan())
        }
    }

    private suspend fun decodeQrCode(qrCode : String?) : QrCode{
        if (qrCode == null) return QrCode.InvalidQrCode

        Log.e(TAG, "qrCode = $qrCode")

        val data: Uri? = Uri.parse(qrCode)

        if (data != null) {
            val queryParamToken = data.getQueryParameter(TOKEN_DEEPLINK_KEY)
            val queryParamProject = data.getQueryParameter(PROJECT_NAME_DEEPLINK_KEY)
            val queryParamModel = data.getQueryParameter(MODEL_DEEPLINK_KEY)

            if (queryParamProject != null && queryParamModel != null && queryParamToken == null) {
                Log.d(TAG, "Project: $queryParamProject")
                Log.d(TAG, "Model: $queryParamModel")

                return QrCode.TemplateQrCode(
                    project = queryParamProject,
                    model = queryParamModel
                )
            }

            if (queryParamToken != null) {
                Log.d(TAG, "Token: $queryParamToken")

                val signedRefResponse = loginService.getSignedRef(
                    shortToken = queryParamToken
                )

                if (signedRefResponse.isFailure) {
                    return QrCode.ExpiredQrCode
                } else {
                    val signedRef = signedRefResponse.getOrNull()

                    if (signedRef != null) {
                        val project = signedRef.projectId
                        val model = signedRef.modelId
                        val accessToken = signedRef.accessToken
                        val idToken = signedRef.idToken

                        return if (project != null && model != null) {
                            QrCode.ProjectQrCode(
                                project = project,
                                model = model,
                                accessToken = accessToken,
                                idToken = idToken
                            )
                        } else {
                            QrCode.LoginQrCode(
                                accessToken = accessToken,
                                idToken = idToken
                            )
                        }
                    } else {
                        return QrCode.ExpiredQrCode
                    }
                }
            }
        }

        return QrCode.InvalidQrCode
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