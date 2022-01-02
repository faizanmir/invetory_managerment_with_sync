package com.fx.inventory.data.db.item

import androidx.room.*
import com.fx.inventory.data.db.BaseDao
import com.fx.inventory.data.models.Item

@Dao
interface ItemDao :BaseDao{
    @Insert
    suspend fun insertItem(item: Item)

    @Query("SELECT * FROM item WHERE localCategoryId =(:cid) ")
    suspend fun getItemsForCategory(cid:Int):List<Item>

    @Query("SELECT * FROM item WHERE localCategoryId =(:cid) AND updated =(:syncStatus)")
    suspend fun getUnSyncedItems(cid:Int,syncStatus:Boolean= false):List<Item>

    @Update
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item:Item)

    @Query("UPDATE item SET updated =(:updated) WHERE itemId =(:itemId)")
    suspend fun setItemUpdated(updated:Boolean =  true, itemId:Int)

    @Query("UPDATE item SET catServerId = (:categoryServerId) WHERE itemId = (:itemId) ")
    suspend fun setItemCategoryServerId(categoryServerId:Int,itemId: Int)

    @Query("SELECT * FROM item ")
    suspend fun getAllItems():List<Item>

    @Query("UPDATE item SET count = (:count) WHERE itemId=(:id)")
    suspend fun updateItemCount(count: Double, id: Int)

    @Query("UPDATE item SET deleted =(:deleted) WHERE itemId=(:id)")
    suspend fun setItemDeleted(deleted:Boolean, id: Int)

    @Query("UPDATE item SET catDeleted =(:catDeleted) WHERE localCategoryId=(:cid)")
    suspend fun setCategoryDeleted(catDeleted:Boolean,cid:Int)

}