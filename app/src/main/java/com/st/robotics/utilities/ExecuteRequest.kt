package com.st.robotics.utilities

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@SuppressWarnings("TooGenericExceptionCaught")
suspend fun <T> executeRequest(
    tag: String = "executeRequest",
    onErrorBlock: suspend (ex: Exception) -> Unit = { /** NOOP **/ },
    block: suspend () -> T
): Result<T> = withContext(Dispatchers.IO) {
    try {
        val data = block()

        Result.success(value = data)
    } catch (ex: Exception) {
        Log.w(tag, ex.message, ex)

        onErrorBlock(ex)

        Result.failure(exception = ex)
    }
}
