package com.fx.inventory.data.repos

import android.util.Log
import com.fx.inventory.base.BaseRepository
import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.db.AppDb
import com.fx.inventory.data.db.item.ItemDao
import com.fx.inventory.data.models.Document
import com.fx.inventory.data.models.Item
import com.fx.inventory.data.models.ItemWrapper
import com.google.gson.JsonElement
import retrofit2.Retrofit
import javax.inject.Inject

class ItemRepository @Inject constructor(dataManager: DataManager,db: AppDb,rf: Retrofit):BaseRepository(dataManager, db,rf) {
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

    suspend fun getItem(id:Int): Item {
        return itemDao.getItem(id);
    }


    suspend fun fetchItemsForCategory(catId: Int): ItemWrapper {
        val response  =  fetchUserDataApi.getItemsForCategory(formHeaderMap(), catId).execute().body()!!
        val itemList  =  ArrayList<Item>()
        if(response.has("items")){
            val jsonItemList = response.getAsJsonArray("items")
            jsonItemList.forEach {
                val name = it.asJsonObject.get("name")
                val rate = it.asJsonObject.get("rate")
                val count =  it.asJsonObject.get("count")
                val files  =  it.asJsonObject.get("files")
                val id= it.asJsonObject.get("id")
                val item = Item(
                    name= name.asString,
                    rate = rate.asDouble,
                    count = count.asDouble,
                    localCategoryId = -1,
                    itemServedId = id.asInt,
                    hasSynced = true,
                    categoryHasSynced = true,
                    catServerId = -1,
                )
                item.files = getFileEntry(files)
                itemList.add(item)
            }
        }
        return ItemWrapper(itemList)
    }


    private fun getFileEntry(files:JsonElement): List<Document> {
        val docs  =  arrayListOf<Document>()
        files.asJsonArray.forEach {
            val url = it.asJsonObject.get("url")
            val id =  it.asJsonObject.get("id")
            val doc  =  Document();
            doc.serverUrl =  url.asString;
            doc.id = id.asInt
            docs.add(doc)
        }
        return docs
    }


    suspend fun deleteItemsWithCatId(cid:Int){
        return itemDao.deleteItemsWithCatId(cid);
    }


}