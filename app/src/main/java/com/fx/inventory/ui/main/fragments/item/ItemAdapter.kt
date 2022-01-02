package com.fx.inventory.ui.main.fragments.item

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.fx.inventory.R
import com.fx.inventory.base.BaseViewHolder
import com.fx.inventory.data.models.Item
import com.fx.inventory.databinding.EmptyListItemBinding
import com.fx.inventory.databinding.ListItemItemBinding

class ItemAdapter constructor(
    val onItemClick: (Item) -> Unit,
    val onItemDelete: (Item) -> Unit,
    val onItemEdit: (Item) -> Unit,
    val onCountIncrement: (Item) -> Unit,
    val onCountDecrement: (Item) -> Unit

) : RecyclerView.Adapter<BaseViewHolder>() {
    var itemList: ArrayList<Item> = arrayListOf()

    companion object {
        const val DELETED_ITEM = 0
        const val NORMAL_ITEM = 1
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(itemList: List<Item>) {
        this.itemList.clear()
        this.itemList.addAll(itemList)
        notifyDataSetChanged()
    }


    inner class EmptyViewHolder(binding: EmptyListItemBinding) : BaseViewHolder(binding) {
        override fun onBind() {

        }
    }


    inner class ItemViewHolder(private val binding: ListItemItemBinding) : BaseViewHolder(binding) {

        override fun onBind() {

            val viewDataHolder = ItemViewDataHolder(
                itemList[adapterPosition],
                onItemLongClick = {
                    showPopUpMenu(binding.root.context, binding.root, itemList[adapterPosition])
                },
                onItemClick = {
                    onItemClick(it)
                },
                incrementItemCount = {
                    onCountIncrement(it)
                },
                decrementItemCount = {
                    onCountDecrement(it)
                },
            )
            binding.ivdh = viewDataHolder

        }

        private fun showPopUpMenu(context: Context, view: View, item: Item) {
            val popupMenu = PopupMenu(context, view)
            popupMenu.gravity = Gravity.CENTER_HORIZONTAL
            popupMenu.inflate(R.menu.item_menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edit -> {
                        onItemEdit(item)
                    }
                    R.id.delete -> {
                        onItemDelete(item)
                    }
                }
                return@setOnMenuItemClickListener false
            }

            popupMenu.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == NORMAL_ITEM) {
            ItemViewHolder(
                ListItemItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            EmptyViewHolder(EmptyListItemBinding.inflate(LayoutInflater.from(parent.context)))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemList[position].deleted||itemList[position].catDeleted) {
            DELETED_ITEM
        } else {
            NORMAL_ITEM
        }
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int = itemList.size

}