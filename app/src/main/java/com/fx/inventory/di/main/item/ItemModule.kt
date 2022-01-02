package com.fx.inventory.di.main.item

import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.db.AppDb
import com.fx.inventory.data.repos.ItemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ItemModule {

    @Provides
    fun provideItemRepository(db: AppDb,dataManager: DataManager):ItemRepository{
        return ItemRepository(dataManager, db);
    }
}