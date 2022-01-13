package com.fx.inventory.data.models

import com.google.gson.annotations.SerializedName

class ItemWrapper(@SerializedName("items") var catItems:List<Item>)
