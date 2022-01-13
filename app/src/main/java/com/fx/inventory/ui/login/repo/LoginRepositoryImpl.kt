package com.fx.inventory.ui.login.repo

import android.util.Log
import com.fx.inventory.base.BaseRepository
import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.db.AppDb
import com.fx.inventory.data.login.web.LoginWebService
import com.fx.inventory.data.models.LoginModel
import com.fx.inventory.data.repos.CategoryRepository
import com.fx.inventory.data.repos.ItemRepository
import retrofit2.Retrofit
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    dm: DataManager,
    db:AppDb,
    private val loginWebService: LoginWebService,
    retrofit: Retrofit
) : LoginRepository, BaseRepository(dm,db,retrofit) {
    companion object {
        private const val TAG = "LoginRepositoryImpl"
    }


    override suspend fun performLogin(email: String, password: String): LoginModel {
        return loginWebService.performLogin(email, password)
    }

    override fun storeEmail(email: String) {
        dataManager.storeEmail(email)
    }

    override fun storePassword(password: String) {
        dataManager.storePassword(password)
    }





    override fun testInjection() {
        Log.e(TAG, "testInjection: injection successful")
    }

    override suspend fun performItemFetch() {
        if(db.categoryDao().getAll().isEmpty()){


        }
    }
}