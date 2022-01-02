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

    @Query("select * from item where catDeleted =(:catDeleted) and hasSynced =(:hasSynced)")
    suspend fun getItemsWhereCategoryDeleteStatusAndSyncStatus(catDeleted: Boolean,hasSynced:Boolean):List<Item>

    @Query("update item set categoryHasSynced =(:hasCategorySynced) where localCategoryId =(:cid)")
    suspend fun updateItemsForCategorySyncStatus(hasCategorySynced:Boolean,cid:Int)

    @Query("select * from item where categoryHasSynced =(:hasCategorySynced)")
    suspend fun getItemsForSyncedCategory(hasCategorySynced:Boolean):List<Item>

    @Query("update item set itemServedId =(:itemServerId) where itemId =(:id)")
    suspend fun updateItemServerId(itemServerId:Int,id:Int)

    @Query("update item set hasSynced=(:synced) where itemId =(:itemId)")
    suspend fun setItemSynced(synced:Boolean,itemId: Int)



}