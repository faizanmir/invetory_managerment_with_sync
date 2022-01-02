package com.fx.inventory.data.login.web

import com.fx.inventory.data.models.LoginModel
import com.google.gson.JsonObject
import retrofit2.Retrofit
import javax.inject.Inject

class LoginWebService @Inject constructor(retrofit: Retrofit){
    private var loginApi  = retrofit.create(LoginApi::class.java)
    suspend fun performLogin(email:String,password:String):LoginModel  = loginApi.performLogin(JsonObject().also {
        it.addProperty("email",email)
        it.addProperty("password",password)
    })
}