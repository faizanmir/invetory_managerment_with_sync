package com.fx.inventory.ui.main.fragments.item_details

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fx.inventory.base.BaseViewHolder
import com.fx.inventory.data.models.Document
import com.fx.inventory.databinding.EmptyListItemBinding
import com.fx.inventory.databinding.GridItemItemImageBinding

class DocumentGridAdapter(private val onDocumentDeleteClick:(Document)->Unit) : RecyclerView.Adapter<BaseViewHolder>() {

    companion object{
        const val DELETED =0;
        const val NORMAL  =1;
    }

    inner class EmptyViewHolder(binding: EmptyListItemBinding) : BaseViewHolder(binding) {
        override fun onBind() {

        }
    }



    var documentList = mutableListOf<Document>()


    @SuppressLint("NotifyDataSetChanged")
    fun updateData(documentList: List<Document>) {
        this.documentList.clear()
        this.documentList.addAll(documentList)
        //notifyItemRangeInserted(0, this.documentList.size)
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        if (documentList[position].deleted){
            return DELETED
        }
        return NORMAL
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if(viewType== NORMAL) {
            DocumentViewHolder(
                GridItemItemImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }else{
            EmptyViewHolder(EmptyListItemBinding.inflate(LayoutInflater.from(parent.context)))
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int {
        return documentList.size
    }

    inner class DocumentViewHolder(private val gridItemItemImageBinding: GridItemItemImageBinding) :
        BaseViewHolder(gridItemItemImageBinding) {

        override fun onBind() {
            if (documentList[adapterPosition].deleted.not()) {
                gridItemItemImageBinding.dgivdh = DocumentGridItemViewDataHolder {
                    onDocumentDeleteClick(documentList[adapterPosition])
                }

                if (documentList[adapterPosition].serverUrl.isNotEmpty()) {
                    Glide.with(gridItemItemImageBinding.root.context)
                        .load(documentList[adapterPosition].serverUrl)
                        .into(gridItemItemImageBinding.documentImageView)
                } else {
                    Glide.with(gridItemItemImageBinding.root.context)
                        .load(Uri.parse(documentList[adapterPosition].filePath))
                        .into(gridItemItemImageBinding.documentImageView)
                }
            }
        }

    }

}