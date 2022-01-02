package com.fx.inventory.ui.main.fragments.item

import com.fx.inventory.data.models.Item

class ItemViewDataHolder(
    val item: Item,
    val onItemClick: (item: Item) -> Unit,
    val onItemLongClick: (item: Item) -> Unit,
    val incrementItemCount:(item:Item)->Unit,
    val decrementItemCount:(item:Item)->Unit,
) {

    fun onItemClick() {
        onItemClick(item)
    }

    fun onItemLongPress(): Boolean {
        onItemLongClick(item)
        return true
    }

    fun decrementCount(){
      decrementItemCount(item)
    }

    fun incrementItemCount(){
        incrementItemCount(item)
    }

}