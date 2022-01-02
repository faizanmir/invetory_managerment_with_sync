package com.fx.inventory.base

import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.db.AppDb
import com.fx.inventory.data.db.BaseDao
import com.fx.inventory.data.db.category.CategoryDao
import com.fx.inventory.data.db.item.ItemDao
import com.fx.inventory.data.db.item.document.DocumentDao
import kotlinx.coroutines.flow.Flow

abstract class BaseRepository(var dataManager: DataManager,val db: AppDb) {

    open suspend fun getAuthToken(): Flow<String?> {
        return dataManager.getAuthToken()
    }

    open fun setAuthToken(authToken: String) {
        dataManager.setAuthToken(authToken)
    }



    open fun getDaoForClassType(clazz:Class<*>):BaseDao?{
        return when {
            clazz.isAssignableFrom(CategoryDao::class.java) -> {
                db.categoryDao();
            }
            clazz.isAssignableFrom(ItemDao::class.java) -> {
                db.itemDao();
            }
            clazz.isAssignableFrom(DocumentDao::class.java)->{
                db.documentDao()
            }
            else -> {
                null
            }
        }
    }


}