package com.fx.inventory.sync

import com.fx.inventory.data.models.Response
import com.google.gson.JsonObject
import retrofit2.http.*

interface SyncApi {
    @POST("/add-category")
    suspend fun uploadCategory(@Body category: JsonObject): Response

    @PUT("/update-category/{categoryId}")
    suspend fun updateCategory(
        @Path(value = "categoryId") catId: Int,
        @Body category: JsonObject
    ): Response

    @DELETE("/delete-category/{id}")
    suspend fun deleteItem(@Path(value = "id") id: Int): Response

    //item
    @POST("/{itemCategoryId}/add-item")
    suspend fun uploadItem(@Path("itemCategoryId") cid: Int, @Body item: JsonObject): Response

    @PUT("/{itemCategoryId}/update-item/{id}")
    suspend fun updateItem(
        @Path("itemCategoryId") cid: Int,
        @Path("id") itemId: Int,
        @Body item: JsonObject
    ): Response

    @DELETE("/{itemCategoryId}/delete-item/{id}")
    suspend fun deleteCategory(
        @Path("itemCategoryId") cid: Int,
        @Path("id") itemId: Int,
        @Body id: Int
    ): Response


}