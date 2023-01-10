package com.example.project3.api

import com.example.project3.constants.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface ColorApi {

    @GET(Constants.GET_URl)
    suspend fun getResults(
        @Query("url")
        url: String,
        @Query("models")
        model: String,
        @Query("api_user")
        apiUser: String,
        @Query("api_secret")
        apiSecret: String
    ): retrofit2.Response<com.example.project3.models.colorApimodels.Response>
}