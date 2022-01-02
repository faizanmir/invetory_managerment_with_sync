package com.fx.inventory.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fx.inventory.data.db.category.CategoryDao
import com.fx.inventory.data.db.item.ItemDao
import com.fx.inventory.data.db.item.document.DocumentDao
import com.fx.inventory.data.models.Category
import com.fx.inventory.data.models.Document
import com.fx.inventory.data.models.Item

@Database(entities = [Category::class, Item::class,Document::class], version = 16, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun itemDao():ItemDao
    abstract fun documentDao():DocumentDao
}