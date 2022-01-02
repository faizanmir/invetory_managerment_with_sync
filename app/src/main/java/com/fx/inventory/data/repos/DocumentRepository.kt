package com.fx.inventory.data.repos

import com.fx.inventory.base.BaseRepository
import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.db.AppDb
import com.fx.inventory.data.db.item.document.DocumentDao
import com.fx.inventory.data.models.Document

class DocumentRepository(dm: DataManager, db:AppDb) :BaseRepository(dm,db) {
    private var documentDao:DocumentDao  =  getDaoForClassType(DocumentDao::class.java) as DocumentDao

    suspend fun addDocument(document:Document){
        documentDao.saveDocument(document)
    }


    suspend fun deleteDocument(document: Document){
        documentDao.deleteDocument(document)
    }

    suspend fun getAllDocumentsForItem(itemId:Int): List<Document> {
        return documentDao.getDocumentsForItem(itemId)
    }
}