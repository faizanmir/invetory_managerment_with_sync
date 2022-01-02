package com.fx.inventory.ui.main.fragments.category

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.fx.inventory.R
import com.fx.inventory.base.BaseViewHolder
import com.fx.inventory.data.models.Category
import com.fx.inventory.databinding.EmptyListItemBinding
import com.fx.inventory.databinding.ListItemCategoryBinding
import com.fx.inventory.ui.main.fragments.item.ItemAdapter

class CategoryRecyclerViewAdapter(val onCategoryClicked:(Category)->Unit,val onItemEditPressed:(Category)->Unit,val onItemDelete:(Int)->Unit) :RecyclerView.Adapter<BaseViewHolder>(){
    var categoryList:List<Category>  = arrayListOf()


    companion object {
        const val DELETED_ITEM = 0
        const val NORMAL_ITEM = 1
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCategoryList(categoryList:List<Category>){
        this.categoryList =  categoryList;
       // notifyItemRangeChanged(0,categoryList.size)
        notifyDataSetChanged()
    }


    inner class EmptyViewHolder(binding: EmptyListItemBinding) : BaseViewHolder(binding) {
        override fun onBind() {

        }
    }



    inner class ViewHolder(private val binding: ListItemCategoryBinding):BaseViewHolder(binding) {

        override fun onBind() {
            if(categoryList[adapterPosition].deleted.not()) {
                val viewDataHolder = CategoryViewDataHolder(categoryList[adapterPosition],
                    onItemCLick = {
                        onCategoryClicked(it);
                    },
                    onItemLongClick = {
                        showPopUpMenu(
                            binding.root.context,
                            binding.root,
                            categoryList[adapterPosition]
                        )
                        true
                    })
                binding.cvdh = viewDataHolder;
            }
        }


        @RequiresApi(Build.VERSION_CODES.M)
        private fun showPopUpMenu(context: Context, view: View, category: Category) {
            val popupMenu = PopupMenu(context, view)
            popupMenu.gravity =  Gravity.CENTER_HORIZONTAL
            popupMenu.inflate(R.menu.item_menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edit -> {
                        onItemEditPressed(category)
                    }
                    R.id.delete -> {
                        onItemDelete(category.cid)
                    }
                }
                return@setOnMenuItemClickListener false
            }

            popupMenu.show()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if(viewType== NORMAL_ITEM) {
            val inflater = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_category, parent, false)
            val listItemCategoryBinding = ListItemCategoryBinding.bind(inflater)
            ViewHolder(listItemCategoryBinding)
        }else{
            EmptyViewHolder(EmptyListItemBinding.inflate(LayoutInflater.from(parent.context)))
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount():Int =categoryList.size


    override fun getItemViewType(position: Int): Int {
        return if (categoryList[position].deleted) {
            ItemAdapter.DELETED_ITEM
        } else {
            ItemAdapter.NORMAL_ITEM
        }
    }
}