package com.st.robotics.api

import com.st.robotics.models.dataset.CreateBlobResponse
import com.st.robotics.models.dataset.Dataset
import com.st.robotics.models.dataset.GetDatasetsResponse
import com.st.robotics.models.dataset.GetDatasetsStatusResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DatasetApi {

    @GET("datasets")
    suspend fun getDatasets(
        @Query("next-token")
        nextToken: String? = null,
        @Query("hollow")
        hollow: Boolean = true,
        @Query("filter")
        filter: String = "public|neq|true"
    ): GetDatasetsResponse

    @GET("datasets/{dataset_id}")
    suspend fun getDataset(
        @Path("dataset_id") datasetId: String
    ): Dataset

    @GET("datasets/{dataset_id}/source-blobs")
    suspend fun getDatasetsStatus(
        @Path("dataset_id") datasetId: String
    ): GetDatasetsStatusResponse

    @POST("blobs")
    suspend fun createBlob(
        @Query("append") datasetId: String,
        @Query("processor") processor: String = "binary-hsd",
        @Query("name") acquisitionName: String,
        @Query("exists-ok") allowDuplicates: Boolean = false,
        @Body request: RequestBody
    ): CreateBlobResponse
}