package com.st.robotics.models.dataset

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CreateBlobResponse(
    val id: String,
    @SerialName("content_type")
    val contentType: String,
    val size: Long,
    val created: String,
    val modified: String
)
