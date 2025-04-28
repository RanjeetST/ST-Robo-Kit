package com.st.robotics.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.robotics.models.QrCode
import com.st.robotics.utilities.QrCodeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val blueManager: BlueManager,
    private val qrCodeService: QrCodeService,
) : ViewModel(){

    private val _isLoading = MutableStateFlow(value = false)
    val isLoading = _isLoading.asStateFlow()

    fun startScan(){
        viewModelScope.launch {
            _isLoading.emit(value = true)

            val value = decodeQrCode(qrCodeService.scan())

            when (value) {
                is QrCode.LoginQrCode -> {
                    vespucciSession.setQrCodeToken(token = value.accessToken)
                    vespucciSession.setQrCodeIdToken(token = value.idToken)
                    _isLoggedIn.emit(value = true)
                }

                is QrCode.ProjectQrCode -> {
                    appAnalyticsService.forEach { service ->
                        service.trackQRCodeScanFlow(projectList.value.firstOrNull { it.name == value.project }?.displayName ?: value.project, AIoTCraftWorkspaceType.PERSONAL.value)
                    }
                    vespucciSession.setQrCodeToken(token = value.accessToken)
                    vespucciSession.setQrCodeIdToken(token = value.idToken)
                    vespucciSession.setProjectName(projectName = value.project)
                    vespucciSession.setModelName(modelName = value.model)
                    vespucciSession.setIsTemplate(isTemplate = false)
                    vespucciService.shouldGoToMLC(goToMLC = true)
                    _isLoggedIn.emit(value = true)

                    _qrCodeResult.emit(value = value)
                }

                is QrCode.TemplateQrCode -> {
                    appAnalyticsService.forEach { service ->
                        service.trackQRCodeScanFlow(sampleProjectList.value.firstOrNull { it.name == value.project }?.displayName ?: value.project, AIoTCraftWorkspaceType.EXAMPLES.value)
                    }
                    vespucciSession.setProjectName(projectName = value.project)
                    vespucciSession.setModelName(modelName = value.model)
                    vespucciSession.setIsTemplate(isTemplate = true)
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