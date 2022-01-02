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

}