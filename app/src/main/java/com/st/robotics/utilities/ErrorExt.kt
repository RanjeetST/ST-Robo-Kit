package com.st.robotics.utilities

import android.content.Context
import com.st.robotics.R
import retrofit2.HttpException
import java.net.UnknownHostException

 fun Result<Any?>.getError(context: Context): String {
    val exception = exceptionOrNull()
    return when {
        exception is UnknownHostException ->
            context.getString(R.string.no_network_connection)

        exception is HttpException && exception.code() == 401 ->
            context.getString(R.string.unauthorized)

        else -> this.exceptionOrNull()?.localizedMessage ?: ""
    }
}