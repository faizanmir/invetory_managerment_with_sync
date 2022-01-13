package com.fx.inventory.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


//int? id,
//String? categoryName,
//bool? requiresAction

@Entity(indices = [Index(value = ["serverId"], unique = true)])
data class Category(
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    @SerializedName("id") var serverId: Int = System.currentTimeMillis().toInt(),
    @ColumnInfo @SerializedName("categoryName") var categoryName: String,
    @ColumnInfo @SerializedName("requiresAction") var requiresAction: Boolean,
    @ColumnInfo var updated: Boolean = false,
    @ColumnInfo var deleted:Boolean =  false,
    @PrimaryKey(autoGenerate = true) var cid: Int = 0,
    @ColumnInfo var hasSynced:Boolean =  false
)


class CategoryResponse (
    @SerializedName("categories")var categories: List<Category> = ArrayList(),
    @SerializedName("success") var success:Boolean = false,
    @SerializedName("message") var message: String? = null
)