package com.fx.inventory.ui.main.fragments.item_details

data class DocumentGridItemViewDataHolder(val onItemDeleteClicked:()->Unit){

    fun onDeleteClicked(){
        onItemDeleteClicked()
    }
}
