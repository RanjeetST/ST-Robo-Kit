package com.st.robotics.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.services.fw_version.FwVersionBoard
import com.st.blue_sdk.services.ota.FwUploadError
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
    @ApplicationContext context: Context
) : ViewModel(){

    companion object {
        const val TAG = "FwDownloadViewModel"
        private const val SEC_IN_MILLIS = 1000f
        private const val BUFFER_ARRAY_SIZE = 4 * 1024
        private const val OTA_NODE_ID: Byte = 0x86.toByte()
    }

    private val contentResolver = context.contentResolver
    private val filesDir = context.filesDir

    private val _fwUpdateState: MutableStateFlow<FwUpdateState> = MutableStateFlow(FwUpdateState())
    val fwUpdateState: StateFlow<FwUpdateState> = _fwUpdateState.asStateFlow()

    private val _errorMessageCode: MutableStateFlow<Int> = MutableStateFlow(-1)
    val errorMessageCode: StateFlow<Int> = _errorMessageCode.asStateFlow()

    fun changeErrorMessageCode(newErrorMessageCode: Int) {
        _errorMessageCode.value = newErrorMessageCode
    }

    fun getNode(nodeId: String){
        val node = blueManager.getNode(nodeId)
        viewModelScope.launch {
            blueManager.upgradeFw(nodeId)
        }

        if (node != null) {
            Log.d(TAG,"Firmware URL : " + node.fwUpdate?.fota?.fwUrl.toString())
        }
    }


//    private val otaListener = object : FwUpdateListener {
//        private var time: Long? = null
//
//        override fun onUpdate(progress: Float) {
//            Log.d(TAG, "update progress $progress")
//            if (time == null) {
//                time = System.currentTimeMillis()
//            }
//            _fwUpdateState.value =
//                _fwUpdateState.value.copy(isInProgress = true, progress = progress)
//        }
//
//        override fun onComplete() {
//            Log.d(TAG, "COMPLETE")
//
//            val duration = time?.let {
//                (System.currentTimeMillis() - it) / SEC_IN_MILLIS
//            } ?: 0f
//
//            _fwUpdateState.value =
//                _fwUpdateState.value.copy(
//                    isComplete = true,
//                    duration = duration
//                )
//        }
//
//        override fun onError(error: FwUploadError) {
//            _fwUpdateState.value =
//                _fwUpdateState.value.copy(isInProgress = false, progress = null, error = error)
//        }
//    }
//
//    fun changeFile(uri: Uri) {
//        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
//            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
//            cursor.moveToFirst()
//
//            _fwUpdateState.value = _fwUpdateState.value.copy(
//                fwUri = uri,
//                downloadFinished = true,
//                fwName = cursor.getString(nameIndex),
//                fwSize = cursor.getLong(sizeIndex).toString()
//            )
//
//            changeErrorMessageCode(-1)
//        }
//    }
//
//    fun startUpgradeFW(
//        nodeId: String,
//        boardType: WbOTAUtils.WBBoardType?,
//        fwType: FirmwareType?,
//        address: String?,
//        nbSectorsToErase: String?
//    ) {
//        viewModelScope.launch {
//            _fwUpdateState.value = _fwUpdateState.value.copy(isInProgress = true)
//
//            _fwUpdateState.value.fwUri?.let {
//                val fileDescriptor =
//                    FwFileDescriptor(fileUri = it, resolver = contentResolver)
//
//                val params = if (boardType != null && fwType != null && address != null && nbSectorsToErase != null) {
//                    CharacteristicFwUpgrade.buildFwUpgradeParams(
//                        firmwareType = fwType,
//                        boardType = boardType,
//                        fileDescriptor = fileDescriptor,
//                        address = address,
//                        nbSectorsToErase = nbSectorsToErase
//                    )
//                } else {
//                    null
//                }
//
//                blueManager.upgradeFw(nodeId = nodeId)?.launchFirmwareUpgrade(
//                    nodeId = nodeId,
//                    fwType = fwType ?: FirmwareType.BOARD_FW,
//                    fileDescriptor = fileDescriptor,
//                    params = params,
//                    fwUpdateListener = otaListener
//                )
//            }
//        }
//    }
//
//    fun clearFwUpdateState() {
//        _fwUpdateState.value = FwUpdateState()
//    }
//
//    fun startDemo(nodeId: String, fwUrl: String) {
//        viewModelScope.launch {
//            if (_fwUpdateState.value.fwUri == null) {
//                if(fwUrl.isNotEmpty()) {
//                    _fwUpdateState.value = _fwUpdateState.value.copy(downloadFinished = false)
//                    try {
//                        val responseBody = downloadAPI.downloadFile(fwUrl).body()
//                        val fileName: String = fwUrl.substring(fwUrl.lastIndexOf("/") + 1)
//                        val filePath = filesDir.absolutePath + fileName
//                        val downloaded = saveFile(responseBody, filePath)
//                        Uri.fromFile(File(filePath))?.let {
//                            var fwSize = ""
//                            if (downloaded) {
//                                val fileDescriptor =
//                                    FwFileDescriptor(fileUri = it, resolver = contentResolver)
//                                fwSize = fileDescriptor.getFileSize().toString()
//
//                                _fwUpdateState.value = _fwUpdateState.value.copy(
//                                    fwUri = it,
//                                    downloadFinished = downloaded,
//                                    fwName = fileName,
//                                    fwSize = fwSize,
//                                    boardInfo = blueManager.getFwVersion(nodeId = nodeId)
//                                )
//                            } else {
//                                _fwUpdateState.value = _fwUpdateState.value.copy(
//                                    fwName = fileName,
//                                    error = FwUploadError.ERROR_DOWNLOADING_FILE,
//                                    boardInfo = blueManager.getFwVersion(nodeId = nodeId)
//                                )
//                            }
//                        }
//                    } catch (e: Exception) {
//                        Log.e(TAG, e.toString())
//                    }
//                }
//            }
//        }
//    }
//
//    private fun saveFile(body: ResponseBody?, path: String): Boolean {
//        if (body != null) {
//            var input: InputStream? = null
//            try {
//                input = body.byteStream()
//                // val file = File(getCacheDir(), "cacheFileAppeal.srl")
//                val fos = FileOutputStream(path)
//                fos.use { output ->
//                    val buffer = ByteArray(BUFFER_ARRAY_SIZE) // or other buffer size
//                    var read: Int
//                    while (input.read(buffer).also { read = it } != -1) {
//                        output.write(buffer, 0, read)
//                    }
//                    output.flush()
//                }
//                return true
//            } catch (e: Exception) {
//                Log.e(TAG, e.toString())
//            } finally {
//                input?.close()
//            }
//        }
//        return false
//    }
//
//    fun stopDemo(nodeId: String) {
//        Log.d(TAG, "stop demo for node $nodeId")
//        /** NOOP **/
//    }
//
//    fun isWbOta(nodeId: String): Boolean =
//        blueManager.getFwUpdateStrategy(nodeId = nodeId) == UpgradeStrategy.CHARACTERISTIC
//
//    fun boardModel(nodeId: String): Boards.Model? =
//        blueManager.getNode(nodeId = nodeId)?.boardType
//
//    fun isRebootedYet(nodeId: String): Boolean {
//        val node = blueManager.getNode(nodeId = nodeId)
//        return (node?.advertiseInfo != null && (node.advertiseInfo!!.getDeviceId() == OTA_NODE_ID || node.familyType == Boards.Family.WBA_FAMILY))
//    }

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
