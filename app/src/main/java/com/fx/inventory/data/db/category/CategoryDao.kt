package com.fx.inventory.data.db.category

import androidx.room.*
import com.fx.inventory.data.db.BaseDao
import com.fx.inventory.data.models.Category

@Dao
interface CategoryDao : BaseDao {
    @Query("SELECT * FROM category")
    suspend fun getAll(): List<Category>

    @Query("SELECT * FROM category WHERE cid IN (:categories)")
    suspend fun loadAllByIds(categories: IntArray): List<Category>

    @Insert
    suspend fun insertAll(vararg users: Category)

    @Delete
    suspend fun delete(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Query("UPDATE category SET serverId = (:categoryServerId) WHERE cid = (:cid) ")
    suspend fun updateServerId(categoryServerId: Int, cid: Int)


    @Query("UPDATE category SET updated =(:updated) WHERE cid  =(:cid)")
    suspend fun setCategoryUpdated(updated: Boolean, cid: Int)


    @Query("UPDATE category SET deleted  = (:deleted) WHERE cid = (:cid) ")
    suspend fun setDeleted(deleted: Boolean, cid: Int)

    @Query("UPDATE category SET requiresAction =(:requiresAction) where cid=(:cid)")
    suspend fun updateActionRequired(requiresAction: Boolean, cid: Int)


    @Query("UPDATE category SET hasSynced =(:sync) WHERE cid =(:cid)")
    suspend fun setSynced(sync:Boolean,cid:Int)

    @Query("SELECT * FROM CATEGORY WHERE hasSynced=(:hasSynced) AND deleted=(:deleted)")
    suspend fun getSyncedDeletedCategories(hasSynced:Boolean,deleted: Boolean):List<Category>

    @Query("SELECT * FROM CATEGORY WHERE hasSynced=(:hasSynced) AND updated=(:updated)")
    suspend fun getSyncedUpdatedCategories(hasSynced:Boolean,updated:Boolean):List<Category>

    @Query("SELECT * FROM CATEGORY WHERE hasSynced(:hasSynced) AND deleted=(:deleted)")
    suspend fun getCategoriesForSyncStatus(hasSynced: Boolean,deleted:Boolean= false):List<Category>

}