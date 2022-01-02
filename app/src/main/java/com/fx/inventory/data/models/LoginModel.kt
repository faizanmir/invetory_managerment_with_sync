package com.fx.inventory.data.models

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("message")val message:String,
    @SerializedName("success")val success:Boolean,
    @SerializedName("jwtToken")val jwtToken:String )
