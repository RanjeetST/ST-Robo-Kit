package com.st.robotics.viewModels

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.BlueManagerImpl
import com.st.blue_sdk.features.extended.pnpl.PnPL
import com.st.blue_sdk.features.extended.pnpl.request.PnPLCmd
import com.st.blue_sdk.models.ConnectionStatus
import com.st.robotics.R
import com.st.robotics.models.app.DataLog
import com.st.robotics.models.dataset.DatasetStatus
import com.st.robotics.models.dataset.Status
import com.st.robotics.utilities.LoginService
import com.st.robotics.utilities.LoginSession
import com.st.robotics.utilities.downloadDirectory
import com.st.robotics.utilities.formatDate
import com.st.robotics.utilities.getError
import com.st.robotics.utilities.readAcquisitionName
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply
import retrofit2.HttpException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import javax.inject.Inject

@HiltViewModel
class DatalogViewModel  @Inject constructor(
    @ApplicationContext private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val blueManager: BlueManager,
    private val loginService: LoginService,
    private val loginSession: LoginSession
) : ViewModel(){
    val isTemplate = loginSession.getLastVespucciSession().isTemplate
    private var _sdJob: Job? = null
    private var _ftpJob: Job? = null
    private var _observeFeatureJob: Job? = null
    private val pnplFeatures: MutableList<PnPL> = mutableListOf()
//    private val errorWiFiString = context.getString(R.string.st_datalogList_wifiErrorDialogMsg)
    private val errorFtpListingString = context.getString(R.string.st_datalogList_errorFtpListing)
//    private val errorFtpDeleteString = context.getString(R.string.st_datalogList_errorFtpDelete)
    private val errorFtpDownloadString = context.getString(R.string.st_datalogList_errorFtpDownload)
    private val absolutePath = context.externalCacheDir?.absolutePath
    private val _dataLogs: MutableStateFlow<List<FTPFile>> = MutableStateFlow(value = emptyList())
    private val _sdDataLogs: MutableStateFlow<Map<File, List<File>>> =
        MutableStateFlow(value = emptyMap())
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
    private val _error: MutableStateFlow<String?> = MutableStateFlow(value = null)
    private val _success: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
    private val _progress: MutableStateFlow<Pair<Float, Pair<String, String>>> =
        MutableStateFlow(value = 0f to ("" to ""))
    private val _connectionStatus = MutableStateFlow<ConnectionStatus?>(value = null)
    private val _uploadedDatalog: MutableStateFlow<List<DatasetStatus>> =
        MutableStateFlow(value = emptyList())
    private var _ftpClient: FTPClient? = null

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var skipWifi = true

    val ftpDataLogs = _dataLogs.asStateFlow().map { ftpFiles ->
        ftpFiles.mapNotNull { file ->
            try {
                DataLog(
                    label = file.name.formatDate(),
                    name = file.name
                )
            } catch (ex: ParseException) {
                Log.w(TAG, "Error parsing file: ${file.name}")

                null
            }
        }.reversed()
    }
    val sdDataLogs = _sdDataLogs.asStateFlow().map { sdFiles ->
        sdFiles.mapNotNull { entry ->
            try {
                DataLog(
                    label = entry.key.name.formatDate(),
                    name = entry.key.name
                )
            } catch (ex: ParseException) {
                Log.w(TAG, "Error parsing file: ${entry.key.name}")

                null
            }
        }.reversed()
    }

    val isLoading = _isLoading.asStateFlow()
    val uploadedDatalog = _uploadedDatalog.asStateFlow()
    val error = _error.asStateFlow()
    val success = _success.asStateFlow()
    val vespucciSessionUpdates = loginSession.vespucciSessionUpdates
    val progress = _progress.asStateFlow()
    val connectionStatus = _connectionStatus.asStateFlow()

    init {
        fetchDatasetsStatus()
    }

    private fun fetchDatasetsStatus() {
        viewModelScope.launch {
            _uploadedDatalog.update {
                loginService.getDatasetsStatus(
                    datasetId = loginSession.getLastVespucciSession().datasetId
                ).getOrNull() ?: emptyList()
            }
        }
    }

    private suspend fun uploadDatalog(
        acquisitionName: String,
        blobs: List<File>
    ){
        val datasetStatus = _uploadedDatalog.value.find { it.name == acquisitionName }

        if(datasetStatus == null || datasetStatus.status == Status.FAILED) {
            val results = loginService.uploadDatalog(
                acquisitionName = acquisitionName,
                datasetId = loginSession.getLastVespucciSession().datasetId,
                files = blobs
            )

            if (results.isSuccess) {
                fetchDatasetsStatus()

                _success.emit(value = true)
            } else {
                _error.emit(value = results.getError(context = context))
            }

            return
        }

        if(datasetStatus.status == Status.SUCCEEDED) {
            _error.update {
                context.getString(R.string.st_datalogList_alreadySucceeded)
            }
        }

        if (datasetStatus.status == Status.RUNNING) {
            _error.update {
                context.getString(R.string.st_datalogList_alreadyRunning)
            }
        }
    }

    private fun sdFetchDatalog() {
        _sdJob?.cancel()
        _sdJob = viewModelScope.launch(Dispatchers.IO) {
            _isLoading.emit(value = true)

            try {
                _sdDataLogs.emit(
                    value =
                        readFilesFromFolder(
                            context = context,
                            rootUri = loginSession.getLastVespucciSession().sdUri
                        )
                )
            } catch (ex: IOException) {
                Log.w(TAG, ex.message, ex)

                _error.emit(value = errorFtpListingString)
            } finally {
                _isLoading.emit(value = false)
            }
        }
    }

    fun setSdUri(sdUri: Uri) {
        loginSession.setSdUri(sdUri = sdUri)
    }

    private fun readFilesFromFolder(context: Context, rootUri: Uri): Map<File, List<File>> {
        val dirs = mutableMapOf<File, List<File>>()
        val contentResolver = context.contentResolver
        val uri = DocumentsContract.buildChildDocumentsUriUsingTree(
            rootUri,
            DocumentsContract.getTreeDocumentId(rootUri)
        )

        val cursor = contentResolver.query(
            uri,
            arrayOf(
                MediaStore.Files.FileColumns.DOCUMENT_ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.SIZE
            ),
            null,
            null,
            null
        )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val files = mutableListOf<File>()
                val id = cursor.getString(0)
                val fileName = cursor.getString(1)
                val fileSize = cursor.getLong(2)
                Log.d("TEST", "id = $id, fileName = $fileName, fileSize = $fileSize")

                val isStFolder = runCatching {
                    fileName.formatDate()
                }

                if (isStFolder.isSuccess) {
                    val localFolder = File("${context.externalCacheDir?.absolutePath}/${fileName}")
                    if (localFolder.exists().not()) {
                        localFolder.mkdirs()
                    }

                    val childUri = DocumentsContract.buildChildDocumentsUriUsingTree(rootUri, id)

                    val childCursor = contentResolver.query(
                        childUri,
                        arrayOf(
                            MediaStore.Files.FileColumns.DOCUMENT_ID,
                            MediaStore.Files.FileColumns.DISPLAY_NAME,
                            MediaStore.Files.FileColumns.SIZE
                        ),
                        null,
                        null,
                        null
                    )

                    if (childCursor != null) {
                        while (childCursor.moveToNext()) {
                            val childId = childCursor.getString(0)
                            val childFileName = childCursor.getString(1)
                            val childFileSize = childCursor.getLong(2)
                            Log.d(
                                "TEST",
                                "childId = $childId, childFileName = $childFileName, childFileSize = $childFileSize"
                            )

                            val f = copyFromFile(
                                context,
                                DocumentsContract.buildDocumentUriUsingTree(rootUri, childId),
                                "$fileName/$childFileName"
                            )
                            f?.let { files.add(it) }
                        }
                        childCursor.close()
                    }
                    dirs[localFolder] = files
                }
            }
            cursor.close()
        }

        return dirs
    }

    private fun copyFromFile(context: Context, uri: Uri, name: String): File? {
        val parcelFileDescriptor =
            context.contentResolver.openFileDescriptor(uri, "r")
        val inputStream =
            FileInputStream(parcelFileDescriptor!!.fileDescriptor)

        val outputFile = File("${context.externalCacheDir?.absolutePath}/${name}")
        val outputStream =
            FileOutputStream(outputFile)
        try {
            inputStream.copyTo(outputStream)

            return outputFile
        } catch (ex: IOException) {
            Log.w(TAG, ex.message, ex)
        } finally {
            parcelFileDescriptor.close()
        }

        return null
    }

    private fun getFtpClient(): FTPClient? {
        return try {
            val ftpClientExpired = try {
                _ftpClient?.listFiles()
                false
            } catch (ex: Exception) {
                viewModelScope.launch {
                    _errorMessage.emit("FTP client expired: ${ex.message}")
                }
                Log.w(TAG, "FTP client expired: ${ex.message}", ex)
                true
            }

            if (_ftpClient == null || ftpClientExpired) {
                val ftpClient = FTPClient()
                val ftpServer = loginSession.getLastVespucciSession().ip

                if (ftpServer.isEmpty()) {
                    viewModelScope.launch {
                        _errorMessage.emit("Invalid FTP server address.")
                    }
                    Log.e(TAG, "Invalid FTP server address.")
                    return null
                }

                ftpClient.connect(ftpServer,
                    FTP_PORT
                )

                if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
                    viewModelScope.launch {
                        _errorMessage.emit("${ftpClient.replyString}")
                    }
                    Log.e(TAG, "FTP connection failed: ${ftpClient.replyString}")
                    return null
                }

                System.setProperty(FTPClient.FTP_SYSTEM_TYPE,
                    FTP_SYSTEM_TYPE_WINDOWS
                )
                System.setProperty(FTPClient.FTP_SYSTEM_TYPE_DEFAULT,
                    FTP_SYSTEM_TYPE_WINDOWS
                )
                ftpClient.login(
                    FTP_USR,
                    FTP_PSW
                )

                if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
                    viewModelScope.launch {
                        _errorMessage.emit("FTP login failed: ${ftpClient.replyString}")
                    }
                    Log.e(TAG, "FTP login failed: ${ftpClient.replyString}")
                    return null
                }

                ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
                ftpClient.enterLocalPassiveMode()
                ftpClient.bufferSize = 0

                _ftpClient = ftpClient
            }

            _ftpClient
        } catch (ex: SocketTimeoutException) {
            viewModelScope.launch {
                _errorMessage.emit("FTP connection failed. Please make sure you're connected to your phone's local hotspot and try again.")
            }
            Log.e(TAG, "FTP connection timed out: ${ex.message}", ex)
            null
        } catch (ex: Exception) {
            viewModelScope.launch {
                _errorMessage.emit("${ex.message}")
            }
            Log.e(TAG, "FTP connection error: ${ex.message}", ex)
            null
        }
    }

    private suspend fun downloadDatalog(
        isActive: Boolean,
        datalog: DataLog
    ): Pair<String, List<File>>? {
        return if (!skipWifi) {
            val ftpClient = getFtpClient()

            if (ftpClient == null) {
                viewModelScope.launch {
                    _errorMessage.emit("FTP Client does not exist. Cannot proceed with download.")
                }
                Log.e(TAG, "FTP Client is null. Cannot proceed with download.")
                return null
            }

            _dataLogs.value
                .find { it.name.equals(other = datalog.name, ignoreCase = true) }
                ?.let { remoteFilePath ->
                    downloadDirectory(
                        isActive = isActive,
                        ftpClient = ftpClient,
                        remoteDirPath = remoteFilePath.name,
                        localDirPath = "$absolutePath/${datalog.name}"
                    ) { progress, fileName ->
                        Log.d(
                            TAG,
                            "download ${datalog.name} fileName: $fileName progress: $progress"
                        )
                        _progress.tryEmit(value = progress to (datalog.name.formatDate() to fileName))
                    }
                }
        } else {
            _sdDataLogs.value.keys
                .find { it.name.equals(other = datalog.name, ignoreCase = true) }
                ?.let { folder ->
                    val files = _sdDataLogs.value[folder] ?: emptyList()

                    val result = readAcquisitionName(files = files)

                    _progress.tryEmit(value = 1f to (datalog.name.formatDate() to ""))

                    result
                }
        }
    }

    private fun ftpSyncDatalog(datalog: DataLog) {
        _ftpJob?.cancel()
        _ftpJob = viewModelScope.launch(Dispatchers.IO) {
            _progress.emit(value = 0f to ("" to ""))
            _success.emit(value = false)

            try {
                downloadDatalog(isActive = isActive, datalog = datalog)?.let { uuidAndZipFile ->
                    uploadDatalog(
                        acquisitionName = datalog.name,
                        blobs = uuidAndZipFile.second
                    )
                }
            } catch (ex: Exception) {
                Log.w(TAG, ex.message, ex)
                _error.emit(value = errorFtpDownloadString)
            }
        }
    }

    private fun sdSyncDatalog(datalog: DataLog) {
        _sdJob?.cancel()
        _sdJob = viewModelScope.launch(Dispatchers.IO) {
            _progress.emit(value = 0f to ("" to ""))
            _success.emit(value = false)

            try {
                downloadDatalog(isActive = isActive, datalog = datalog)?.let { uuidAndZipFile ->
                    uploadDatalog(
                        acquisitionName = datalog.name,
                        blobs = uuidAndZipFile.second
                    )
                }
            } catch (ex: Exception) {
                Log.w(TAG, ex.message, ex)
                _error.emit(value = errorFtpDownloadString)
            }
        }
    }

    fun syncDatalog(datalog: DataLog) {
        if (loginSession.getLastVespucciSession().isTemplate) {
            _error.tryEmit(value = context.getString(R.string.st_datalogList_errorTemplate))
        } else {
            if (!skipWifi) {
                ftpSyncDatalog(datalog = datalog)
            } else {
                sdSyncDatalog(datalog = datalog)
            }
        }
    }

    companion object {
        private const val TAG = "DatalogViewModel"

        private const val TIMEOUT_WIFI_CONNECT = 25000L
        private const val DELAY_CMD = 350L
        private const val DELAY_FTP = 10L
        private const val RESET_IP = "0.0.0.0"
        private const val FTP_USERNAME_JSON_KEY = "ftp_username"
        private const val FTP_PASSWORD_JSON_KEY = "set_ftp_credentials"
        private const val WIFI_CONNECT_JSON_KEY = "wifi_connect"
        private const val IP_JSON_KEY = "ip"
        private const val SSID_JSON_KEY = "ssid"
        private const val PSW_JSON_KEY = "password"
        private const val WIFI_CONFIG_JSON_KEY = "wifi_config"
        private const val WIFI_DISCONNECT_JSON_KEY = "wifi_disconnect"
        private const val FTP_USR = "st"
        private const val FTP_PSW = "st"
        private const val FTP_PORT = 21
        private const val FTP_SYSTEM_TYPE_WINDOWS = "WINDOWS"
        val WIFI_CONFIG = PnPLCmd(command = "get_status", request = "wifi_config")
    }

}