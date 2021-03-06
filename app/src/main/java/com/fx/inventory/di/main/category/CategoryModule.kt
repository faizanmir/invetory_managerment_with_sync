package com.fx.inventory.di.main.category

import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.db.AppDb
import com.fx.inventory.data.repos.CategoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class CategoryModule {
    @Provides
    fun provideCategoryRepository(db:AppDb,dm:DataManager,retrofit: Retrofit): CategoryRepository {
        return CategoryRepository(db, dm,retrofit)
    }
}