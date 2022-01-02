package com.fx.inventory.ui.login.repo

import com.fx.inventory.data.models.LoginModel
import kotlinx.coroutines.flow.Flow


interface LoginRepository {
    suspend fun performLogin(email:String,password:String): LoginModel;
    suspend fun getAuthToken():Flow<String?>
    fun setAuthToken(authToken:String)
    fun storeEmail(email:String)
    fun storePassword(password:String)
    fun testInjection();
}