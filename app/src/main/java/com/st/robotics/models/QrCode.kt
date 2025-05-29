package com.st.robotics.models

sealed class QrCode {
    data class TemplateQrCode(
        val project: String,
        val model: String
    ) : QrCode()

    data class ProjectQrCode(
        val project: String,
        val model: String,
        val accessToken: String,
        val idToken: String
    ) : QrCode()

    data class LoginQrCode(
        val accessToken: String,
        val idToken: String
    ) : QrCode()

    data object ExpiredQrCode : QrCode()

    data object InvalidQrCode : QrCode()
}