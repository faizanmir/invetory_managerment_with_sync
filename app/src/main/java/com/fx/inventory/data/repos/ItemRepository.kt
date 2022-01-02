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

    suspend fun setItemUpdated(itemId:Int){
        return itemDao.setItemUpdated(true,itemId)
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

}