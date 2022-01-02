package com.fx.inventory.data.datamanager

import kotlinx.coroutines.flow.Flow

interface DataManager {
    fun getAuthToken(): Flow<String?>
    fun setAuthToken( authToken:String?)
    fun testInjection();
    fun storeEmail(email:String)
    fun storePassword(password:String)
    fun retrieveEmail():Flow<String?>
    fun retrievePassword():Flow<String?>
}