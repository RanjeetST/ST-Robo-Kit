package com.st.robotics.models.app

import androidx.annotation.Keep
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import java.util.UUID

@OptIn(InternalSerializationApi::class)
@Keep
@Serializable
data class DataLog(
    val uuid: String = UUID.randomUUID().toString(),
    val label: String,
    val name: String
) {

    companion object {

        @SuppressWarnings("MagicNumber")
        fun mock() = DataLog(
            label = LoremIpsumMock,
            name = LoremIpsumMock
        )
    }
}

val LoremIpsumMock: String
    get() = LoremIpsum(words = 20).values.joinToString()