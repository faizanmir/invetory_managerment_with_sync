package com.fx.inventory.ui.main.viewModel.interfaces

interface CategoryActionHandler {
    fun onItemClicked(id:Int)
    fun showMessage(message:String)
    fun onAddCategoryPressed()
}