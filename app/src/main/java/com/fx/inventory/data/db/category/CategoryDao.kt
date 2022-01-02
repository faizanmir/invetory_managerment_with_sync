package com.fx.inventory.data.db.category

import androidx.room.*
import com.fx.inventory.data.db.BaseDao
import com.fx.inventory.data.models.Category

@Dao
interface CategoryDao : BaseDao {
    @Query("SELECT * FROM category")
    fun getAll(): List<Category>

    @Query("SELECT * FROM category WHERE cid IN (:categories)")
    fun loadAllByIds(categories: IntArray): List<Category>

    @Insert
    fun insertAll(vararg users: Category)

    @Delete
    fun delete(category: Category)

    @Update
    fun updateCategory(category: Category)

    @Query("UPDATE category SET serverId = (:categoryServerId) WHERE cid = (:cid) ")
    fun updateServerId(categoryServerId: Int, cid: Int)


    @Query("UPDATE category SET updated =(:updated) WHERE cid  =(:cid)")
    fun setCategoryUpdated(updated: Boolean, cid: Int)


    @Query("UPDATE category SET deleted  = (:deleted) WHERE cid = (:cid) ")
    fun setDeleted(deleted: Boolean, cid: Int)

    @Query("UPDATE category SET requiresAction =(:requiresAction) where cid=(:cid)")
    fun updateActionRequired(requiresAction: Boolean, cid: Int)


    @Query("UPDATE category SET hasSynced =(:sync) WHERE cid =(:cid)")
    fun setSynced(sync:Boolean,cid:Int)
}