package com.st.robotics.models

import android.net.Uri

data class LoginSessionData(
    val nodeId: String,
    val name: String,
    val boardId: String,
    val boardName: String,
    val boardType: String,
    val projectName: String,
    val isTemplate: Boolean,
    val isDataset: Boolean,
    val datasetId: String,
    val modelName: String,
    val outputName: String,
    val outputType: String,
    val qrCodeToken: String?,
    val qrCodeIdToken: String?,
    val ip: String,
    val sdUri: Uri
) {
    companion object {
        val EMPTY_SESSION = LoginSessionData(
            nodeId = "",
            boardId = "",
            name = "",
            boardType = "",
            boardName = "",
            projectName = "",
            isTemplate = false,
            isDataset = false,
            datasetId = "",
            modelName = "",
            outputName = "",
            outputType = "",
            qrCodeToken = null,
            qrCodeIdToken = null,
            ip = "",
            sdUri = Uri.EMPTY
        )
    }
}
