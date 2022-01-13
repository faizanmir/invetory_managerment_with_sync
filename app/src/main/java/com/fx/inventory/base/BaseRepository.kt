package com.fx.inventory.base

import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.db.AppDb
import com.fx.inventory.data.db.BaseDao
import com.fx.inventory.data.db.category.CategoryDao
import com.fx.inventory.data.db.item.ItemDao
import com.fx.inventory.data.db.item.document.DocumentDao
import com.fx.inventory.data.remote.FetchUserDataApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.Retrofit

abstract class BaseRepository(var dataManager: DataManager, val db: AppDb,var retrofit: Retrofit) {

    open suspend fun getAuthToken(): Flow<String?> {
        return dataManager.getAuthToken()
    }

    open var fetchUserDataApi: FetchUserDataApi =  retrofit.create(FetchUserDataApi::class.java)

    open fun setAuthToken(authToken: String) {
        dataManager.setAuthToken(authToken)
    }


    open fun getDaoForClassType(clazz: Class<*>): BaseDao? {
        return when {
            clazz.isAssignableFrom(CategoryDao::class.java) -> {
                db.categoryDao()
            }
            clazz.isAssignableFrom(ItemDao::class.java) -> {
                db.itemDao()
            }
            clazz.isAssignableFrom(DocumentDao::class.java) -> {
                db.documentDao()
            }
            else -> {
                null
            }
        }
    }


    open suspend fun formHeaderMap(): Map<String, String> = HashMap<String, String>().apply {
        //this["Accept"] = "application/json"
        this["Content-Type"] = "application/json"
        this["Authorization"] = "Bearer ${getAuthToken().first()}"
    }


}