package com.fx.inventory.data.repos

import com.fx.inventory.base.BaseRepository
import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.db.AppDb
import com.fx.inventory.data.db.category.CategoryDao
import com.fx.inventory.data.models.Category
import javax.inject.Inject

class CategoryRepository @Inject constructor(db: AppDb, dm: DataManager) :
    BaseRepository(dataManager = dm, db = db) {
    private var categoryDao: CategoryDao =
        getDaoForClassType(CategoryDao::class.java) as CategoryDao

    fun storeCategory(category: Category) {
        categoryDao.insertAll(category)
    }

    fun getAllCategories(): List<Category> {
        return categoryDao.getAll()
    }

    fun deleteCategory(category: Category) {
        categoryDao.delete(category)
    }

    fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }

    fun getCategoryById(cid: Int): List<Category> {
        return categoryDao.loadAllByIds(IntArray(1, init = { cid }))
    }

    fun updateServerId(categoryServerId:Int,cid:Int){
        categoryDao.updateServerId(categoryServerId, cid)
    }


    fun setDeleted(cid:Int){
        categoryDao.setDeleted(true,cid)
    }

    fun setUpdated(cid:Int){
        categoryDao.setCategoryUpdated(true,cid)
    }


    fun setSynced(cid:Int){
        categoryDao.setSynced(true,cid)
    }


}