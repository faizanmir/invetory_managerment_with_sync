package com.fx.inventory.data.datamanager

import kotlinx.coroutines.flow.Flow

interface DataManager {
    fun getAuthToken(): Flow<String?>
    fun setAuthToken( authToken:String?)
    fun testInjection();
}