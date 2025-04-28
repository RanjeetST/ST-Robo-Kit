package com.st.robotics.utilities

import android.content.SharedPreferences
import android.net.Uri
import com.st.blue_sdk.board_catalog.di.Preferences
import com.st.robotics.models.LoginSessionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginSession @Inject constructor(
    @Preferences private val loginPreferences: SharedPreferences
) {
    private val _loginSessionUpdates =
        MutableStateFlow(value = LoginSessionData.EMPTY_SESSION)

    val vespucciSessionUpdates = _loginSessionUpdates.asStateFlow()

     fun setNodeId(nodeId: String) {
        _loginSessionUpdates.update {
            it.copy(nodeId = nodeId)
        }
    }

     fun setDeviceName(name: String) {
        _loginSessionUpdates.update {
            it.copy(name = name)
        }
    }

     fun setQrCodeToken(token: String) {
        _loginSessionUpdates.update {
            it.copy(qrCodeToken = token)
        }

        loginPreferences.edit()
            .putString(TOKEN_PREF_KEY, token)
            .apply()
    }

     fun logout() {
        _loginSessionUpdates.update {
            it.copy(
                qrCodeToken = null,
                qrCodeIdToken = null
            )
        }
    }

     fun setQrCodeIdToken(token: String) {
        _loginSessionUpdates.update {
            it.copy(qrCodeIdToken = token)
        }

        loginPreferences.edit()
            .putString(ID_TOKEN_PREF_KEY, token)
            .apply()
    }

     fun setIsTemplate(isTemplate: Boolean) {
        _loginSessionUpdates.update {
            it.copy(isTemplate = isTemplate)
        }
    }

     fun setIsDataset(isDataset: Boolean) {
        _loginSessionUpdates.update {
            it.copy(isDataset = isDataset)
        }
    }

     fun setBoardName(boardId: String, boardName: String) {
        _loginSessionUpdates.update {
            it.copy(
                boardId = boardId,
                boardName = boardName
            )
        }
    }

     fun setBoardType(boardType: String) {
        _loginSessionUpdates.update {
            it.copy(boardType = boardType)
        }
    }

     fun setProjectName(projectName: String) {
        _loginSessionUpdates.update {
            it.copy(projectName = projectName)
        }
    }

     fun setDatasetId(datasetId: String) {
        _loginSessionUpdates.update {
            it.copy(datasetId = datasetId)
        }
    }

     fun setModelName(modelName: String) {
        _loginSessionUpdates.update {
            it.copy(modelName = modelName)
        }
    }

     fun setOutputName(outputName: String) {
        _loginSessionUpdates.update {
            it.copy(outputName = outputName)
        }
    }

     fun setOutputType(outputType: String) {
        _loginSessionUpdates.update {
            it.copy(outputType = outputType)
        }
    }

     fun setIp(ip: String) {
        _loginSessionUpdates.update {
            it.copy(ip = ip)
        }
    }

     fun setSdUri(sdUri: Uri) {
        _loginSessionUpdates.update {
            it.copy(sdUri = sdUri)
        }
    }

     fun getLastVespucciSession(): LoginSessionData =
        _loginSessionUpdates.value

    init {
        setQrCodeToken(
            token = loginPreferences.getString(
                TOKEN_PREF_KEY, ""
            ) ?: ""
        )

        setQrCodeIdToken(
            token = loginPreferences.getString(
                ID_TOKEN_PREF_KEY, ""
            ) ?: ""
        )
    }

    companion object {
        private const val TOKEN_PREF_KEY = "token"
        private const val ID_TOKEN_PREF_KEY = "idToken"
    }
}