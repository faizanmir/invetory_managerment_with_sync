package com.fx.inventory.sync

import com.fx.inventory.data.models.Response
import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.http.*

interface SyncApi {
    @POST("/add-category")
    suspend fun uploadCategory(@HeaderMap headers:Map<String, String>,@Body category: JsonObject): Response

    @PUT("/update-category/{categoryId}")
    suspend fun updateCategory(
        @HeaderMap headers:Map<String, String>,
        @Path(value = "categoryId") catId: Int,
        @Body category: JsonObject
    ): Response

    @DELETE("/delete-category/{id}")
    suspend fun deleteCategory(@HeaderMap headers:Map<String, String>, @Path(value = "id") id: Int): Response

    //item
    @POST("/{itemCategoryId}/add-item")
    suspend fun uploadItem(@HeaderMap headers:Map<String, String>,
                           @Path("itemCategoryId") cid: Int,
                           @Part("name") name: RequestBody,
                           @Part("rate") rate:RequestBody,
                           @Part("count") count:RequestBody):Response

    @PUT("/{itemCategoryId}/update-item/{id}")
    suspend fun updateItem(
        @HeaderMap headers:Map<String, String>,
        @Path("itemCategoryId") cid: Int,
        @Path("id") itemId: Int,
        @Body item: JsonObject
    ): Response

    @DELETE("/{itemCategoryId}/delete-item/{id}")
    suspend fun deleteItem(
        @HeaderMap headers:Map<String, String>,
        @Path("itemCategoryId") cid: Int,
        @Path("id") itemId: Int,
    ): Response


}