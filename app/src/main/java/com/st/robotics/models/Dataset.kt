package com.st.robotics.models

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetDatasetsResponse(
    val items: List<Dataset>,
    @SerialName("next_token")
    val nextToken: String? = null
)

@Serializable
data class Dataset(
    val id: String,
    val name: String,
    val created: String,
    val modified: String,
    val description: String? = null,
    val chunks: List<Chunk>? = null,
    @SerialName("ground_truth_labels")
    val groundTruthLabels: List<String>? = null
) {

    companion object {
        fun mock() = Dataset(
            id = "123",
            name = "Dataset",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            created = "2023-01-01T00:00:00Z",
            modified = "2023-01-01T00:00:00Z",
            groundTruthLabels = listOf("label1", "label2")
        )
    }
}

@Keep
@Serializable
data class ChunkSelectorLabels(
    @SerialName("device_name")
    val deviceName: String? = null,
    @SerialName("firmware_name")
    val firmwareName: String? = null,
    @SerialName("blob_name")
    val blobAcquisitionName: String? = null,
    @SerialName("device_os_name")
    val deviceOsName: String? = null,
    @SerialName("firmware_alias")
    val firmwareAlias: String? = null,
    @SerialName("firmware_version")
    val firmwareVersion: String? = null,
    @SerialName("device_architecture")
    val deviceArchitecture: String? = null,
    @SerialName("firmware_part_number")
    val firmwarePartNumber: String? = null
)

@Serializable
data class Chunk(
    val id: String,
    val created: String,
    val modified: String,
    @SerialName("source_blob")
    val sourceBlob: Blob,
    @SerialName("selector_labels")
    val selectorLabels: ChunkSelectorLabels
)

@Serializable
data class Blob(
    val id: String,
    val type: String? = null,
    val size: Int
)
