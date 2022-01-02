package com.fx.inventory.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder(viewBinding: ViewBinding) :RecyclerView.ViewHolder(viewBinding.root){
    abstract fun onBind()
}