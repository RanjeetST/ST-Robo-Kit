package com.st.robotics.api

import com.st.robotics.models.SignedRef
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface SecretsApi {

    @GET("secrets/signedref")
    @Headers("content-type: binary/octet-stream")
    suspend fun getSignedRef(
        @Header("signedref") shortToken: String
    ): SignedRef
}
