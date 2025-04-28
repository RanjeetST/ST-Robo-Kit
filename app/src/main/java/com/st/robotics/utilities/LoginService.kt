package com.st.robotics.utilities

import com.st.robotics.models.AiProject
import com.st.robotics.models.Dataset
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.util.Locale
import javax.inject.Inject

class LoginService @Inject constructor(

) {
    private val cacheProjects = mutableMapOf<String, AiProject>()
    private var _goToMLC = false

     suspend fun getDataset(datasetId: String): Result<Dataset> =
        executeRequest(tag = TAG) {
            datasetApi.getDataset(datasetId = datasetId)
        }

     suspend fun getDatasetsStatus(datasetId: String): Result<List<DatasetStatus>> =
        executeRequest(tag = TAG) {
            datasetApi.getDatasetsStatus(datasetId = datasetId).items
        }

     suspend fun sendAIoTCraftTrackingInfo(): Result<ResponseBody> =
        executeRequest(tag = TAG) {
            val jsonMediaType = "application/json; charset=utf-8".toMediaType()
            val json = """{"location": ${isoToM49[Locale.getDefault().country]}}"""
            val requestBody: RequestBody = json.toRequestBody(jsonMediaType)
            trackingApi.trackingInfo(requestBody)
        }

     suspend fun getDatasets(nextToken: String?): Result<GetDatasetsResponse> =
        executeRequest(tag = TAG) {
            datasetApi.getDatasets(nextToken = nextToken)
        }

     suspend fun uploadDatalog(
        acquisitionName: String,
        datasetId: String,
        files: List<File>
    ): Result<CreateBlobResponse> = executeRequest(tag = TAG) {
        val requestBody =
            MultipartBody.Builder().setType(MultipartBody.FORM).apply {
                files.forEach { file ->
                    addFormDataPart(
                        name = "name",
                        filename = file.name,
                        body = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    )
                }
            }.build()

        datasetApi.createBlob(
            datasetId = datasetId,
            acquisitionName = acquisitionName,
            request = requestBody
        )
    }

     suspend fun getAiProjects(): Result<List<AiProject>> =
        executeRequest(tag = TAG) {
            projectApi.getAiProjects().also { response ->
                cacheProjects.putAll(response.map { it.name to it })
            }
        }

     val goToMLC: Boolean
        get() = _goToMLC

     fun shouldGoToMLC(goToMLC: Boolean) {
        _goToMLC = goToMLC
    }

     suspend fun getSignedRef(shortToken: String): Result<SignedRef> =
        executeRequest(tag = TAG) {
            secretsApi.getSignedRef(shortToken = shortToken)
        }

     suspend fun getSampleProjects(): Result<List<AiProject>> =
        executeRequest(tag = TAG) {
            projectExampleAuthApi.getSampleProjects().also { response ->
                cacheProjects.putAll(response.map { it.name to it })
            }
        }

     suspend fun downloadUCF(
        isTemplate: Boolean,
        projectName: String,
        modelName: String,
        outputFileName: String
    ): Result<ResponseBody> = executeRequest(tag = TAG) {
        if (isTemplate) {
            projectApi.downloadTemplateUcf(
                projectName = projectName,
                model = modelName,
                outputFileName = outputFileName
            )
        } else {
            projectApi.downloadUcf(
                projectName = projectName,
                model = modelName,
                outputFileName = outputFileName
            )
        }
    }

     suspend fun getAiProject(
        projectName: String,
        forceFetch: Boolean,
        isTemplate: Boolean
    ): Result<AiProject?> =
        executeRequest(tag = TAG) {
            if (forceFetch) {
                if(isTemplate){
                    projectExampleAuthApi.getSampleProjects().find { it.name == projectName }
                }else{
                    projectApi.getAiProjects().find { it.name == projectName }
                }
            } else {
                cacheProjects.get(key = projectName)
            }
        }

    companion object {
        private const val TAG = "VespucciService"
    }
}