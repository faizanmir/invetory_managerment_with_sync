package com.fx.inventory.ui.login.repo

import android.util.Log
import com.fx.inventory.base.BaseRepository
import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.db.AppDb
import com.fx.inventory.data.login.web.LoginWebService
import com.fx.inventory.data.models.LoginModel
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    dm: DataManager,
    db:AppDb,
    private val loginWebService: LoginWebService
) : LoginRepository, BaseRepository(dm,db) {
    companion object {
        private const val TAG = "LoginRepositoryImpl"
    }


    override suspend fun performLogin(email: String, password: String): LoginModel {
        return loginWebService.performLogin(email, password)
    }

    override fun testInjection() {
        Log.e(TAG, "testInjection: injection successful")
    }
}