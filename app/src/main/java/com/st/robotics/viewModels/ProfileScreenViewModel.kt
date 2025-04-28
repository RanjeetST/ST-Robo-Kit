package com.st.robotics.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.robotics.models.LoginSessionData
import com.st.robotics.models.QrCode
import com.st.robotics.utilities.LoginSession
import com.st.robotics.utilities.QrCodeService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val blueManager: BlueManager,
    private val qrCodeService: QrCodeService,
    private val loginSession: LoginSession,
) : ViewModel(){

    private val _isLoading = MutableStateFlow(value = false)
    val isLoading = _isLoading.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(value = false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _error = MutableStateFlow<String?>(value = null)
    val errorMessage = _error.asStateFlow()


    fun startScan(){
        viewModelScope.launch {
            _isLoading.emit(value = true)

            val value = decodeQrCode(qrCodeService.scan())

            when (value) {
                is QrCode.LoginQrCode -> {
                    loginSession.setQrCodeToken(token = value.accessToken)
                    loginSession.setQrCodeIdToken(token = value.idToken)
                    _isLoggedIn.emit(value = true)
                }

                is QrCode.ProjectQrCode -> {
                    appAnalyticsService.forEach { service ->
                        service.trackQRCodeScanFlow(projectList.value.firstOrNull { it.name == value.project }?.displayName ?: value.project, AIoTCraftWorkspaceType.PERSONAL.value)
                    }
                    loginSession.setQrCodeToken(token = value.accessToken)
                    loginSession.setQrCodeIdToken(token = value.idToken)
                    loginSession.setProjectName(projectName = value.project)
                    loginSession.setModelName(modelName = value.model)
                    loginSession.setIsTemplate(isTemplate = false)
                    vespucciService.shouldGoToMLC(goToMLC = true)
                    _isLoggedIn.emit(value = true)

                    _qrCodeResult.emit(value = value)
                }

                is QrCode.TemplateQrCode -> {
                    appAnalyticsService.forEach { service ->
                        service.trackQRCodeScanFlow(sampleProjectList.value.firstOrNull { it.name == value.project }?.displayName ?: value.project, AIoTCraftWorkspaceType.EXAMPLES.value)
                    }
                    loginSession.setProjectName(projectName = value.project)
                    loginSession.setModelName(modelName = value.model)
                    loginSession.setIsTemplate(isTemplate = true)
                    vespucciService.shouldGoToMLC(goToMLC = true)

                    _qrCodeResult.emit(value = value)
                }

                QrCode.ExpiredQrCode -> {
                    _error.emit(value = context.getString(R.string.st_qrCode_expiredError))
                }

                QrCode.InvalidQrCode -> {
                    _error.emit(value = context.getString(R.string.st_qrCode_invalidError))
                }
            }
        }
    }

    fun decodeQrCode(qrCode : String?){

    }
}