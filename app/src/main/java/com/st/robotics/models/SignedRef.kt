package com.st.robotics.models

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class SignedRef(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("id_token")
    val idToken: String,
    @SerialName("model")
    val modelId: String? = null,
    @SerialName("project")
    val projectId: String? = null
)
