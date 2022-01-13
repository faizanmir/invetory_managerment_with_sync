package com.fx.inventory.data.models

import androidx.room.*
import com.google.gson.annotations.SerializedName
import kotlin.random.Random

@Entity(


    indices = [Index(value = ["localCategoryId"]), Index(
        value = ["itemServedId"],
        unique = true
    ), Index(value = ["catServerId"])],
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["cid"],
        childColumns = ["localCategoryId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Category::class,
        parentColumns = ["serverId"],
        childColumns = ["catServerId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    ),
    ],

)

data class Item constructor(
    @PrimaryKey(autoGenerate = true) val itemId: Int = 0,
    @SerializedName("name")@ColumnInfo var name: String,
    @SerializedName("rate")@ColumnInfo var rate: Double,
    @SerializedName("count")@ColumnInfo var count: Double,
    @ColumnInfo var localCategoryId: Int,
    @ColumnInfo var updated: Boolean = false,
    @ColumnInfo var catServerId: Int,
    @SerializedName("id")@ColumnInfo val itemServedId: Int = Random(System.currentTimeMillis()).nextInt(),
    @ColumnInfo var deleted: Boolean = false,
    @ColumnInfo var catDeleted: Boolean = false,
    @ColumnInfo var hasSynced:Boolean = false,
    @ColumnInfo var categoryHasSynced:Boolean,
){
    @Ignore @SerializedName("files") var files:List<Document> = ArrayList()

}
