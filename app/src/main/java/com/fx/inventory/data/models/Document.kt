package com.fx.inventory.data.models

import androidx.room.*

@Entity(
    indices = [Index(value = ["localItemId"]),Index("itemServedId")],
 foreignKeys = [
     ForeignKey(
         entity = Item::class,
         parentColumns = ["itemId"],
         childColumns = ["localItemId"],
     ),
    ForeignKey(
        entity =Item::class,
        parentColumns = ["itemServedId"],
        childColumns = ["itemServedId"]
    )
 ]
)
data class Document(
    @ColumnInfo var localItemId: Int,
    @ColumnInfo var itemServedId: Int ,
    @PrimaryKey(autoGenerate = true) var id: Int =0,
    @ColumnInfo var filePath:String,
    @ColumnInfo var requiresSync:Boolean = false,
    @ColumnInfo var serverUrl:String = ""
)
