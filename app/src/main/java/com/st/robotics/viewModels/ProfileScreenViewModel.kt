package com.st.robotics.viewModels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.features.activity.Activity
import com.st.robotics.R
import com.auth0.jwt.JWT
import com.st.robotics.models.AiProject
import com.st.robotics.models.LoginSessionData
import com.st.robotics.models.QrCode
import com.st.robotics.models.dataset.Dataset
import com.st.robotics.utilities.LoginService
import com.st.robotics.utilities.LoginSession
import com.st.robotics.utilities.getError
import com.st.robotics.utilities.QrCodeService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val blueManager: BlueManager,
    private val qrCodeService: QrCodeService,
    private val loginSession: LoginSession,
    private val loginService: LoginService,
) : ViewModel(){

    private var activityResultRegistryOwner: ActivityResultRegistryOwner? = null

    private val _isLoading = MutableStateFlow(value = false)
    val isLoading = _isLoading.asStateFlow()

//    private val _isLoggedIn = MutableStateFlow(value = false)
    private val _isLoggedIn = MutableStateFlow(value = loginSession.vespucciSessionUpdates.value.qrCodeToken != "")
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _error = MutableStateFlow<String?>(value = null)
    val errorMessage = _error.asStateFlow()

    private val _qrCodeResult = MutableStateFlow<QrCode?>(value = null)
    val qrCodeResult = _qrCodeResult.asStateFlow()

    private val _sampleProjects = MutableStateFlow<List<AiProject>>(value = emptyList())
    val sampleProjectList = _sampleProjects.asStateFlow()

    private val _datasetList = MutableStateFlow<List<Dataset>>(value = emptyList())
    val datasetList = _datasetList.asStateFlow()

    private val _nextPage = MutableStateFlow<String?>(value = null)
    val nextPage = _nextPage.asStateFlow()

    private val _projects = MutableStateFlow<List<AiProject>>(value = emptyList())
    val projectList = _projects.asStateFlow()

    private val _email = MutableStateFlow(value = "")
    val email = _email.asStateFlow()

    fun login() {
        viewModelScope.launch {
            activityResultRegistryOwner?.let {
                _isLoggedIn.value =
                    false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {

            loginSession.logout()
            _isLoggedIn.emit(value = false)
        }
    }

    fun fetchProjects(){
        viewModelScope.launch {
            _isLoading.emit(value = true)

            val result = loginService.getSampleProjects()

            if(result.isFailure){
                _error.emit(value = result.getError(context))
            } else {
                _sampleProjects.emit(value = result.getOrDefault(defaultValue = emptyList()))
            }

            _isLoading.emit(value = false)
        }

        if (_isLoggedIn.value) {
            viewModelScope.launch {
                _isLoading.emit(value = true)

                val result = loginService.getAiProjects()

                if (result.isFailure) {
                    _error.emit(value = result.getError(context = context))
                    logout()
                } else {
                    _projects.emit(value = result.getOrDefault(defaultValue = emptyList()))
                }

                _isLoading.emit(value = false)
            }

            viewModelScope.launch {
                _isLoading.emit(value = true)
                _nextPage.emit(value = null)
                _datasetList.emit(value = emptyList())

                val result = loginService.getDatasets(nextToken = null)

                if (result.isFailure) {
                    _error.emit(value = result.getError(context = context))
                    logout()
                } else {
                    _datasetList.emit(value = result.getOrNull()?.items ?: emptyList())
                    _nextPage.emit(value = result.getOrNull()?.nextToken)

                        datasetList.collect { datasets ->
                            Log.d(TAG, "Current datasets: $datasets")
                        }
                }

                _isLoading.emit(value = false)
            }
        }
    }

    fun dismissError() {
        _error.tryEmit(value = null)
    }

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
//                    appAnalyticsService.forEach { service ->
//                        service.trackQRCodeScanFlow(projectList.value.firstOrNull { it.name == value.project }?.displayName ?: value.project, AIoTCraftWorkspaceType.PERSONAL.value)
//                    }
                    loginSession.setQrCodeToken(token = value.accessToken)
                    loginSession.setQrCodeIdToken(token = value.idToken)
                    loginSession.setProjectName(projectName = value.project)
                    loginSession.setModelName(modelName = value.model)
                    loginSession.setIsTemplate(isTemplate = false)
                    loginService.shouldGoToMLC(goToMLC = true)
                    _isLoggedIn.emit(value = true)

                    _qrCodeResult.emit(value = value)
                }

                is QrCode.TemplateQrCode -> {
//                    appAnalyticsService.forEach { service ->
//                        service.trackQRCodeScanFlow(sampleProjectList.value.firstOrNull { it.name == value.project }?.displayName ?: value.project, AIoTCraftWorkspaceType.EXAMPLES.value)
//                    }
                    loginSession.setProjectName(projectName = value.project)
                    loginSession.setModelName(modelName = value.model)
                    loginSession.setIsTemplate(isTemplate = true)
                    loginService.shouldGoToMLC(goToMLC = true)

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

    fun initLoginManager(){
        viewModelScope.launch {
//            activityResultRegistryOwner = activity as ActivityResultRegistryOwner

//            _isLoggedIn.emit(value = false)

            _isLoggedIn.onEach { status ->
                if(status) {
                    val token = loginSession.getLastVespucciSession().qrCodeIdToken
                    val userEmail = try {
                        val decoded = JWT.decode(token)
                        val hasEmail = decoded.claims.keys.contains(JWT_EMAIL_KEY)
                        val hasIdentity = decoded.claims.keys.contains(JWT_IDENTITIES_KEY)

                        when{
                            hasEmail -> decoded.getClaim(JWT_EMAIL_KEY).asString()

                            hasIdentity -> {
                                val identities =
                                    decoded.getClaim(JWT_IDENTITIES_KEY)
                                        .asArray(Map::class.java)

                                if (identities.isNotEmpty() &&
                                    identities[0].containsKey(JWT_USER_ID_KEY)
                                    ) {
                                    identities[0][JWT_USER_ID_KEY] as String
                                } else {
                                    ""
                                }
                            }

                            else -> ""
                        }
                    } catch (ex : Exception){
                        Log.w(TAG,"Error while getting email",ex)

                        ""
                    }

                    _email.update { userEmail }
                }
            }.collect()
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

    fun resetGoToMLC() {
        loginService.shouldGoToMLC(goToMLC = false)
    }

    fun setProjectName(projectName: String) {
        loginSession.setProjectName(projectName = projectName)
        loginSession.setModelName(modelName = "")
    }

    fun setIsTemplate(isTemplate: Boolean) {
        loginSession.setIsTemplate(isTemplate = isTemplate)
        loginService.shouldGoToMLC(goToMLC = isTemplate)
    }

    fun dismissQrCodeResult() {
        _qrCodeResult.tryEmit(value = null)
    }

    fun setIsDataset(isDataset: Boolean) {
        loginSession.setIsDataset(isDataset = isDataset)
    }

    fun setDatasetId(datasetId: String) {
        loginSession.setDatasetId(datasetId = datasetId)
    }

    fun loadNextDataset(nextToken: String?) {
        viewModelScope.launch {
            _isLoading.emit(value = true)

            val result = loginService.getDatasets(nextToken = nextToken)

            if (result.isFailure) {
                _error.emit(value = result.getError(context = context))
            } else {
                _datasetList.update {
                    it + (result.getOrNull()?.items ?: emptyList())
                }
                _nextPage.emit(value = result.getOrNull()?.nextToken)
            }

            _isLoading.emit(value = false)
        }
    }

    fun checkLoginStatus() {
        viewModelScope.launch {
            _isLoggedIn.update {
                 false ||
                        loginSession.getLastVespucciSession().qrCodeToken.isNullOrEmpty().not()
            }
        }
    }

    fun sendEmail(isUserLoggedIn: Boolean) {
        val recipient =
            context.getString(R.string.st_deleteAccount_recipient)
        val subject =
            context.getString(R.string.st_deleteAccount_subject)
        val bodyEmail = if (isUserLoggedIn) "(${_email.value})" else ""
        val body =
            context.getString(R.string.st_deleteAccount_body, bodyEmail)

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            _error.update {
                context.getString(R.string.st_deleteAccount_error)
            }
        }
    }


    companion object {
        private const val TAG = "ProfileScreenViewModel"

        private const val JWT_EMAIL_KEY = "email"
        private const val JWT_USER_ID_KEY = "userId"
        private const val JWT_IDENTITIES_KEY = "identities"
        private const val JWT_NICKNAME_KEY = "nickname"
        private const val TOKEN_DEEPLINK_KEY = "short_token"
        private const val PROJECT_NAME_DEEPLINK_KEY = "project"
        private const val MODEL_DEEPLINK_KEY = "model"
    }
}