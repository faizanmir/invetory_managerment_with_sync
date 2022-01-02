package com.fx.inventory.data.login.web

import com.fx.inventory.data.models.LoginModel
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginApi {
    @Headers("Accept: application/json")
    @POST("/auth/login/")
    suspend fun performLogin(@Body body:JsonObject):LoginModel

}