package com.st.robotics.api

import com.st.robotics.models.AiProject
import retrofit2.http.GET

interface ProjectExampleApi {
    @GET("templates/projects")
    suspend fun getSampleProjects(): List<AiProject>
}

