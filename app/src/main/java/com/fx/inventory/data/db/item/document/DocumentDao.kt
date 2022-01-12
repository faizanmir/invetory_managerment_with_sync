package com.fx.inventory.data.db.item.document

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.fx.inventory.data.db.BaseDao
import com.fx.inventory.data.models.Document

@Dao
interface  DocumentDao :BaseDao{
    @Insert
    suspend fun saveDocument(document:Document)
    @Delete
    suspend fun  deleteDocument(document: Document)

    @Query("SELECT * FROM document WHERE localItemId=(:itemId)")
    suspend fun getDocumentsForItem(itemId:Int):List<Document>

    @Query("UPDATE document SET deleted=(:deleted) where id=(:id)")
    suspend fun markFileForDeletion(deleted:Boolean,id:Int)

    @Query("UPDATE document SET serverUrl=(:url) where id =(:id)")
    suspend fun updateDocument(url:String,id: Int)

    @Query("SELECT * from document")
    suspend fun getAllDocuments():List<Document>

    @Query("UPDATE document set itemServedId =(:itemServerId) where id =(:id)")
    suspend fun updateItemServerId(itemServerId:Int,id:Int)

    @Query("UPDATE document set serverUrl =(:url) , serverId = (:serverId) , hasSynced =(:synced)  where id=(:id)")
    suspend fun updateParams(id: Int,serverId:Int,url: String,synced:Boolean)
}