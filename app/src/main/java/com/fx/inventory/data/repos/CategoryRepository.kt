package com.fx.inventory.data.repos

import com.fx.inventory.base.BaseRepository
import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.db.AppDb
import com.fx.inventory.data.db.category.CategoryDao
import com.fx.inventory.data.models.Category
import com.fx.inventory.data.models.CategoryResponse
import com.fx.inventory.data.models.ItemWrapper
import com.fx.inventory.data.remote.FetchUserDataApi
import retrofit2.Retrofit
import javax.inject.Inject

class CategoryRepository @Inject constructor(db: AppDb, dm: DataManager,var rf: Retrofit) :
    BaseRepository(dataManager = dm, db = db,rf) {
    private var categoryDao: CategoryDao =
        getDaoForClassType(CategoryDao::class.java) as CategoryDao




    suspend fun storeCategory(category: Category) {
        categoryDao.insertAll(category)
    }

    suspend fun getAllCategories(): List<Category> {
        return categoryDao.getAll()
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category)
    }

    suspend fun updateCategory(category: Category) {

        categoryDao.updateCategory(category)
    }


    suspend fun updateServerId(categoryServerId:Int,cid:Int){
        categoryDao.updateServerId(categoryServerId, cid)
    }


    suspend fun setDeleted(cid:Int){
        categoryDao.setDeleted(true,cid)
    }

    suspend fun setUpdated(cid:Int,updated:Boolean){
        categoryDao.setCategoryUpdated(updated,cid)
    }


    suspend fun setSynced(cid:Int,synced:Boolean){
        categoryDao.setSynced(synced,cid)
    }

    suspend fun getSyncedUpdatedCategories(hasSynced:Boolean,updated: Boolean):List<Category>{
        return categoryDao.getSyncedUpdatedCategories(hasSynced, updated)
    }

    suspend fun getSyncedDeletedCategories(hasSynced:Boolean,deleted: Boolean):List<Category>{
        return categoryDao.getSyncedDeletedCategories(hasSynced, deleted)
    }

    suspend fun getCategoriesForSyncStatus(hasSynced: Boolean):List<Category>{
        return categoryDao.getCategoriesForSyncStatus(hasSynced)
    }


    suspend fun fetchCategoriesOnline(): CategoryResponse {
        return fetchUserDataApi.getCategoriesForUser(header = formHeaderMap()).execute().body()!!
    }



}