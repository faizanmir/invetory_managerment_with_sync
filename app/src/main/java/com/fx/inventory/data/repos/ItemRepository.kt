package com.fx.inventory.data.repos

import com.fx.inventory.base.BaseRepository
import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.db.AppDb
import com.fx.inventory.data.db.item.ItemDao
import com.fx.inventory.data.models.Item
import javax.inject.Inject

class ItemRepository @Inject constructor(dataManager: DataManager,db: AppDb):BaseRepository(dataManager, db) {
    private val itemDao:ItemDao =  getDaoForClassType(ItemDao::class.java) as ItemDao

    suspend fun addItem(item:Item){
        itemDao.insertItem(item)
    }
    suspend fun deleteItem(item: Item){
        itemDao.deleteItem(item)
    }

    suspend fun getAllItemsForCategory(catId:Int):List<Item>{
        return itemDao.getItemsForCategory(catId)
    }


    suspend fun getAllItems(): List<Item> {
        return itemDao.getAllItems();
    }

    suspend fun updateItem(item: Item){
        itemDao.updateItem(item)
    }

    suspend fun setItemUpdated(itemId:Int,updated:Boolean){
        return itemDao.setItemUpdated(updated,itemId)
    }

    suspend fun setItemDeleted(id:Int){
        itemDao.setItemDeleted(true,id)
    }

    suspend fun getUnSyncedItemsForCategory(cid:Int): List<Item> {
        return itemDao.getUnSyncedItems(cid);
    }

    suspend fun updateItemCount(count: Double,id:Int) {
        itemDao.updateItemCount(count,id)
    }

    suspend fun setCategoryDeleted(cid:Int){
        itemDao.setCategoryDeleted(true,cid)
    }

    suspend fun getItemsWhereCategoryDeleteStatusAndSyncStatus(catDeleted:Boolean,hasSynced:Boolean):List<Item>{
        return itemDao.getItemsWhereCategoryDeleteStatusAndSyncStatus(catDeleted =catDeleted, hasSynced)
    }

    suspend fun updateItemsForCategorySyncStatus(hasCategorySynced:Boolean,cid:Int){
        return itemDao.updateItemsForCategorySyncStatus(hasCategorySynced,cid)
    }

    suspend fun getItemsForSyncedCategory(hasCategorySynced:Boolean):List<Item>{
        return itemDao.getItemsForSyncedCategory(hasCategorySynced)
    }

    suspend fun updateItemCategoryServerId(categoryServerId:Int, id:Int){
        return itemDao.setItemCategoryServerId(categoryServerId,id)
    }

    suspend fun updateItemServerId(itemServerId:Int, id:Int){
        return itemDao.updateItemServerId(itemServerId,id)
    }

    suspend fun setItemSynced(synced:Boolean,itemId: Int){
        return itemDao.setItemSynced(synced, itemId)
    }


}