package com.st.robotics.api

import com.st.robotics.models.AiProject
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProjectApi {

    @GET("projects/{project}/models/{model}/training")
    suspend fun downloadUcf(
        @Path("project") projectName: String,
        @Path("model") model: String,
        @Query("name") outputFileName: String,
        @Query("type") outputType: String = "artifacts"
    ): ResponseBody

    @GET("projects")
    suspend fun getAiProjects(): List<AiProject>

//    @GET("templates/projects")
//    suspend fun getSampleProjects(): List<AiProject>

    @GET("templates/projects/{project}/models/{model}/training")
    suspend fun downloadTemplateUcf(
        @Path("project") projectName: String,
        @Path("model") model: String,
        @Query("name") outputFileName: String,
        @Query("type") outputType: String = "artifacts"
    ): ResponseBody
}
