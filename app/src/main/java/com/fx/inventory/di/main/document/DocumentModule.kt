package com.fx.inventory.di.main.document

import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.db.AppDb
import com.fx.inventory.data.repos.DocumentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DocumentModule {
    @Provides
    fun provideDocumentRepository(db: AppDb,dataManager: DataManager): DocumentRepository {
        return DocumentRepository(dataManager,db)
    }
}