package com.fx.inventory.data.models

import androidx.room.*
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
data class Item(
    @PrimaryKey(autoGenerate = true) val itemId: Int = 0,
    @ColumnInfo var name: String,
    @ColumnInfo var rate: Double,
    @ColumnInfo var count: Double,
    @ColumnInfo val localCategoryId: Int,
    @ColumnInfo val updated: Boolean = false,
    @ColumnInfo val catServerId: Int,
    @ColumnInfo val itemServedId: Int = Random(System.currentTimeMillis()).nextInt(),
    @ColumnInfo val deleted: Boolean = false,
    @ColumnInfo val catDeleted: Boolean = false,
    @ColumnInfo val hasSynced:Boolean = false,
    @ColumnInfo val categoryHasSynced:Boolean,
)
