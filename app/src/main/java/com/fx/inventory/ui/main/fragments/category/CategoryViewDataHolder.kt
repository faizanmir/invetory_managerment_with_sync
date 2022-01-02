package com.fx.inventory.ui.main.fragments.category

import com.fx.inventory.data.models.Category

class CategoryViewDataHolder(val category: Category, val onItemCLick:(Category)->Unit,val onItemLongClick:(Int)->Boolean) {

    fun onClick()  {
        onItemCLick(category)
    }

    fun onLongClick():Boolean{
     onItemLongClick(category.cid)
        return true
    }
}