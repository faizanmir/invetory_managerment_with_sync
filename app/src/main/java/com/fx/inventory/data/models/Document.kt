package com.fx.inventory.data.models

import androidx.room.*

@Entity(
    indices = [Index(value = ["localItemId"]),Index("itemServedId")],
 foreignKeys = [
     ForeignKey(
         entity = Item::class,
         parentColumns = ["itemId"],
         childColumns = ["localItemId"],
     )
 ]
)
data class Document(
    @ColumnInfo var localItemId: Int,
    @ColumnInfo var itemServedId: Int,
    @PrimaryKey(autoGenerate = true) var id: Int =0,
    @ColumnInfo var filePath:String,
    @ColumnInfo var hasSynced:Boolean = false,
    @ColumnInfo var serverUrl:String = "",
    @ColumnInfo var deleted:Boolean = false,
    @ColumnInfo var serverId:Int= 0,
)
