package com.fx.inventory.data.remote

import com.fx.inventory.data.models.Category
import com.fx.inventory.data.models.CategoryResponse
import com.fx.inventory.data.models.Item
import com.fx.inventory.data.models.ItemWrapper
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface FetchUserDataApi {

    @GET("category/get-all-categories")
     fun getCategoriesForUser(@HeaderMap header:Map<String,String>): Call<CategoryResponse>

    @GET("item/{itemCategoryId}/get-items-for-category-id/")
    fun getItemsForCategory(@HeaderMap headers:Map<String,String>,@Path("itemCategoryId")categoryId:Int):Call<JsonObject>

}