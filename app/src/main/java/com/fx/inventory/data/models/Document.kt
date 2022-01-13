package com.fx.inventory.data.models

import androidx.room.*
import com.google.gson.annotations.SerializedName
import kotlin.random.Random

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
data class Document constructor(
    @ColumnInfo var localItemId: Int = Random(0).nextInt(),
    @ColumnInfo var itemServedId: Int = Random(0).nextInt(),
    @PrimaryKey(autoGenerate = true) var id: Int =0,
    @ColumnInfo var filePath:String ="",
    @ColumnInfo var hasSynced:Boolean = false,
    @SerializedName("url") @ColumnInfo var serverUrl:String = "",
    @ColumnInfo var deleted:Boolean = false,
    @SerializedName("id")@ColumnInfo var serverId:Int= 0,
){
    @Ignore
    @SerializedName("fileType")
    var fileType:String? =null

    @Ignore
    @SerializedName("size")
    var size:Int =0;

    @Ignore
    @SerializedName("fileName")
    var fileName:String = ""
}
