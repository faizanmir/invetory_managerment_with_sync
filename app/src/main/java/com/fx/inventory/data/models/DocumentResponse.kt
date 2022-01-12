package com.fx.inventory.data.models

import com.google.gson.annotations.SerializedName

data class DocumentResponse (
    @SerializedName("url")var url: String? = null,
    @SerializedName("id")var id: Long? = null
)