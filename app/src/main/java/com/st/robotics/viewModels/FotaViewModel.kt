package com.st.robotics.viewModels

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.board_catalog.BoardCatalogRepo
import com.st.blue_sdk.board_catalog.models.BoardFotaType
import com.st.blue_sdk.bt.advertise.getFwInfo
import com.st.blue_sdk.models.Boards
import com.st.blue_sdk.services.NodeServiceConsumer
import com.st.blue_sdk.services.fw_version.FwVersionBoard
import com.st.blue_sdk.services.ota.FirmwareType
import com.st.blue_sdk.services.ota.FwFileDescriptor
import com.st.blue_sdk.services.ota.FwUpdateListener
import com.st.blue_sdk.services.ota.FwUploadError
import com.st.blue_sdk.services.ota.UpgradeStrategy
import com.st.blue_sdk.services.ota.characteristic.CharacteristicFwUpgrade
import com.st.blue_sdk.utils.WbOTAUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FotaViewModel @Inject constructor(
    private val blueManager: BlueManager,
    private val nodeServiceConsumer: NodeServiceConsumer,
    private val catalog: BoardCatalogRepo,
    @ApplicationContext context: Context
) : ViewModel() {

    companion object {
        const val TAG = "FwDownloadViewModel"
        private const val SEC_IN_MILLIS = 1000f
    }

    private val contentResolver = context.contentResolver
    private val filesDir = context.filesDir

    private val _fwUpdateState: MutableStateFlow<FwUpdateState> = MutableStateFlow(FwUpdateState())
    val fwUpdateState: StateFlow<FwUpdateState> = _fwUpdateState.asStateFlow()

    private val _errorMessageCode: MutableStateFlow<Int> = MutableStateFlow(-1)
    val errorMessageCode: StateFlow<Int> = _errorMessageCode.asStateFlow()

    private var _selectedFileUri: Uri? = null
    private var _selectedFileName: String? = null

    val selectedFileUri: Uri?
        get() = _selectedFileUri
    val selectedFileName: String?
        get() = _selectedFileName

    fun onFileSelected(uri: Uri, context: Context) {
        _selectedFileUri = uri
        _selectedFileName = getFileName(uri, context)
        Log.d("Fota", "File Name : $_selectedFileName")
        Log.d("Fota", _selectedFileUri.toString())
    }

    private fun getFileName(uri: Uri, context: Context): String? {
        var name: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }

    private val otaListener = object : FwUpdateListener {
        private var time: Long? = null

        override fun onUpdate(progress: Float) {
            Log.d(TAG, "update progress $progress")
            if (time == null) {
                time = System.currentTimeMillis()
            }
            _fwUpdateState.value =
                _fwUpdateState.value.copy(isInProgress = true, progress = progress)
        }

        override fun onComplete() {
            val duration = time?.let {
                (System.currentTimeMillis() - it) / SEC_IN_MILLIS
            } ?: 0f

            Log.d(TAG, "COMPLETE in $duration")

            _fwUpdateState.value =
                _fwUpdateState.value.copy(
                    isComplete = true,
                    duration = duration
                )
        }

        override fun onError(error: FwUploadError) {
            _fwUpdateState.value =
                _fwUpdateState.value.copy(isInProgress = false, progress = null, error = error)
        }
    }

    suspend fun isFastFota(nodeId: String) {
        val nodeService = nodeServiceConsumer.getNodeService(nodeId)
        val nodeFeatures = nodeService?.getNodeFeatures()
        val upgradeStrategy = blueManager.getFwUpdateStrategy(nodeId)
        val advInfo = nodeService?.getNode()?.advertiseInfo
        val catalogInfo = blueManager.getBoardCatalog()

        catalogInfo.forEach {
            Log.d("TAG",
                "Fota = ${it.fota}" + "Firmware name${it.fwName}" + "Firmware version : ${it.fwVersion}" )
        }
        Log.d("TAG", "Upgrade Strategy: $upgradeStrategy")
        val boardFirmware = advInfo?.getFwInfo()?.let {
            catalog.getFwDetailsNode(it.deviceId, it.fwId)
        }
        Log.d("TAG","Board Firmware : ${boardFirmware?.fota?.type}")
        //Fast Fota is Enabled only for BlueST-SDK V2 board type
        val hasFastFota = boardFirmware?.fota?.type == BoardFotaType.FAST

        Log.d("TAG", hasFastFota.toString())
    }


    fun onSubmit(nodeId: String) {
        viewModelScope.launch {
            val fileDescriptor =
                _selectedFileUri?.let { FwFileDescriptor(fileUri = it, resolver = contentResolver) }

            if (fileDescriptor != null) {
                blueManager.upgradeFw(nodeId)?.launchFirmwareUpgrade(
                    nodeId,
                    FirmwareType.BOARD_FW,
                    fileDescriptor = fileDescriptor,
                    params = null,
                    fwUpdateListener = otaListener
                )
            }
        }
    }

    fun isWbOta(nodeId: String): Boolean =
        blueManager.getFwUpdateStrategy(nodeId = nodeId) == UpgradeStrategy.CHARACTERISTIC

    fun boardModel(nodeId: String): Boards.Model? =
        blueManager.getNode(nodeId = nodeId)?.boardType

    fun changeErrorMessageCode(newErrorMessageCode: Int) {
        _errorMessageCode.value = newErrorMessageCode
    }

    fun getNode(nodeId: String) {
        val node = blueManager.getNode(nodeId)
        viewModelScope.launch {
            blueManager.upgradeFw(nodeId)
        }

        if (node != null) {
            Log.d(TAG, "Firmware URL : " + node.fwUpdate?.fota?.fwUrl.toString())
        }
    }
}

data class FwUpdateState(
    val fwUri: Uri? = null,
    val fwName: String = "",
    val fwSize: String = "",
    val downloadFinished: Boolean = true,
    val boardInfo: FwVersionBoard? = null,
    val isComplete: Boolean = false,
    val duration: Float = 0f,
    val isInProgress: Boolean = false,
    val progress: Float? = null,
    val error: FwUploadError? = null
)
