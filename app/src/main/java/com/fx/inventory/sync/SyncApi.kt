package com.fx.inventory.sync

import com.fx.inventory.data.models.DocumentResponse
import com.fx.inventory.data.models.Response
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface SyncApi {
    @POST("/category/add-category")
    suspend fun uploadCategory(@HeaderMap headers:Map<String, String>,@Body category: JsonObject): Response

    @PUT("/category/update-category/{categoryId}")
    suspend fun updateCategory(
        @HeaderMap headers:Map<String, String>,
        @Path(value = "categoryId",encoded = true) catId: Int,
        @Body category: JsonObject
    ): Response

    @DELETE("/category/delete-category/{id}")
    suspend fun deleteCategory(@HeaderMap headers:Map<String, String>, @Path(value = "id",encoded = true) id: Int): Response

    //item
    @POST("/item/{itemCategoryId}/add-item")
    suspend fun uploadItem(@HeaderMap headers:Map<String, String>,
                           @Path("itemCategoryId",encoded = true) cid: Int,@Body body:JsonObject):Response

    @PUT("/item/{itemCategoryId}/update-item/{id}")
    suspend fun updateItem(
        @HeaderMap headers:Map<String, String>,
        @Path("itemCategoryId",encoded = true) cid: Int,
        @Path("id",encoded = true) itemId: Int,
        @Body item: JsonObject
    ): Response

    @DELETE("/item/{itemCategoryId}/delete-item/{id}")
    suspend fun deleteItem(
        @HeaderMap headers:Map<String, String>,
        @Path("itemCategoryId",encoded = true) cid: Int,
        @Path("id",encoded = true) itemId: Int,
    ): Response


    @POST("/item/{itemId}/add-files-to-item")
    suspend fun addFileToItem(@HeaderMap header:Map<String,String>, @Path("itemId") itemId: Int, @Body file:String):DocumentResponse

    @DELETE("/item/{itemId}/delete-file-from-item/{fileId}")
    suspend fun deleteFileFromItem(@HeaderMap header:Map<String,String>,@Path("itemId")itemId:Int,@Path("fileId")fileId:Int)


}