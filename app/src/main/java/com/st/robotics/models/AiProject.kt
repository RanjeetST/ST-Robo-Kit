package com.st.robotics.models

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Keep
@Serializable
data class AiProject(
    val uuid: String,
    @SerialName("ai_project_name")
    val name: String,
    val models: List<AiModel>,
    @SerialName("display_name")
    val displayName: String? = null,
    val description: String? = null,
    val reference: String? = null,
    val version: String? = null
) {

    @Transient
    val allTags = models.flatMap { it.modelMetadata?.classes ?: emptyList() }.distinct()

    companion object {

        fun mock() = AiProject(
            uuid = "123123123",
            name = "Ai Project",
            displayName = "Ai Project",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            reference = "ref321321321",
            version = "1.0.0",
            models = listOf(
                AiModel.mock()
            )
        )
    }
}

@Keep
@Serializable
data class AiModel(
    val uuid: String,
    val name: String,
    val description: String? = null,
    val dataset: AiProjectDataset? = null,
    @SerialName("metadata")
    val modelMetadata: ModelMetadata? = null,
    val target: Target? = null,
    val training: Training? = null
) {
    companion object {
        fun mock() = AiModel(
            dataset = AiProjectDataset(
                datasetId = "123123123",
                name = "dataset"
            ),
            description = "mock ai model",
            modelMetadata = ModelMetadata(
                classes = listOf("class", "another_class", "a_third_class"),
                type = "classification"
            ),
            name = "model",
            target = Target(
                component = "component",
                device = "device",
                type = "type"
            ),
            training = Training(
                artifacts = listOf("artifact1", "artifact2"),
                configuration = "configuration",
                reports = listOf("report1", "report2"),
                runtime = Runtime(
                    jobId = "jobId",
                    tool = "tool",
                    version = "version"
                )
            ),
            uuid = "123123123"
        )
    }
}

@Keep
@Serializable
data class AiProjectDataset(
    @SerialName("dataset_id")
    val datasetId: String,
    val name: String
)

@Keep
@Serializable
data class ModelMetadata(
    val classes: List<String>,
    val type: String? = null
)

@Keep
@Serializable
data class Target(
    val component: String? = null,
    val device: String? = null,
    val type: String? = null
)

@Keep
@Serializable
data class Training(
    val artifacts: List<String>? = null,
    val configuration: String? = null,
    val reports: List<String>,
    val runtime: Runtime? = null
)

@Keep
@Serializable
data class Runtime(
    @SerialName("job_id")
    val jobId: String? = null,
    val tool: String? = null,
    val version: String? = null
)
