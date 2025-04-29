package com.st.robotics.models.dataset

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetDatasetsStatusResponse(
    val items: List<DatasetStatus>
)

@Serializable
enum class Status {
    @SerialName("Failed")
    FAILED,

    @SerialName("Running")
    RUNNING,

    @SerialName("Succeeded")
    SUCCEEDED
}

@Serializable
data class DatasetStatus(
    @SerialName("blob_id")
    val id: String,
    val name: String? = null,
    val status: Status,
)
