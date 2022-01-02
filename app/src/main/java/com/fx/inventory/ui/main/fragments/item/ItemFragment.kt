package com.fx.inventory.ui.main.fragments.item

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fx.inventory.data.models.Item
import com.fx.inventory.databinding.DialogIncrementDecrementCountBinding
import com.fx.inventory.databinding.DialogItemInputBinding
import com.fx.inventory.databinding.FragmentItemBinding
import com.fx.inventory.ui.main.MainActivity
import com.fx.inventory.ui.main.fragments.item_details.ItemDetailsFragment
import com.fx.inventory.ui.main.viewModel.interfaces.ItemActionHandler
import com.fx.inventory.ui.main.viewModel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemFragment : Fragment(), ItemActionHandler {
    private val viewModel: MainActivityViewModel by activityViewModels()
    lateinit var binding: FragmentItemBinding
    lateinit var adapter: ItemAdapter

    companion object {
        private const val TAG = "ItemFragment"
        fun newInstance(): ItemFragment = ItemFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemBinding.inflate(LayoutInflater.from(activity))
        binding.viewModel = viewModel
        viewModel.itemViewActionHandler = this
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Items"
        viewModel.getAllItemsForCategory()

        adapter = ItemAdapter(
            onItemDelete = {
                viewModel.setItemDeleted(it)
                notifyAdapterForDeletion(item = it)
                 },
            onItemEdit = { showItemContentDialog(it) },
            onItemClick = {
                          viewModel.item =  it;
                switchToItemDetailsFragment()
            },
            onCountIncrement = { showEditCountDialog(it, isDecrement = false) },
            onCountDecrement = { showEditCountDialog(it, isDecrement = true) }
        )

        binding.itemRecyclerView.adapter = adapter

        viewModel.itemListLiveData.observe(viewLifecycleOwner, {
            adapter.updateData(it)
        })
    }

    override fun addItemClicked() {
        showItemContentDialog(null)
    }

    override fun showMessage(message: String) {
        TODO("Not yet implemented")
    }

    private fun showItemContentDialog(item: Item?) {
        val dialogBuilder = AlertDialog.Builder(activity)
        val binding = DialogItemInputBinding.inflate(LayoutInflater.from(activity))
        dialogBuilder.setView(binding.root)
        if (item != null) {
            binding.itemCount.setText(item.count.toString())
            binding.itemName.setText(item.name)
            binding.itemRate.setText(item.rate.toString())
        }
        val dialog = dialogBuilder.create()

        binding.addItemButton.setOnClickListener {
            when {
                binding.itemCount.text.isEmpty() -> {
                    binding.itemCount.error = "Please enter the count"
                }
                binding.itemRate.text.isEmpty() -> {
                    binding.itemCount.error = "Please enter the count"
                }
                binding.itemName.text.isEmpty() -> {
                    binding.itemName.error = "Please enter the item name"
                }

                else -> {
                    if (item == null) {
                        viewModel.saveItemForCategory(
                            binding.itemName.text.toString(),
                            binding.itemRate.text.toString().toDouble(),
                            binding.itemCount.text.toString().toDouble()
                        )
                    } else {
                        item.count = binding.itemCount.text.toString().toDouble()
                        item.rate = binding.itemRate.text.toString().toDouble()
                        item.name = binding.itemName.text.toString()
                        viewModel.updateItem(item)
                        notifyAdapterAboutChangeInItem(item)
                    }
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }


    private fun showEditCountDialog(item: Item, isDecrement: Boolean) {
        val dialogBuilder = AlertDialog.Builder(activity)
        val binding = DialogIncrementDecrementCountBinding.inflate(LayoutInflater.from(context))
        dialogBuilder.setView(binding.root)
        val dialog = dialogBuilder.create()
        binding.changeCountButton.setOnClickListener {
            if (binding.newCount.text.isNotEmpty()) {
                if (isDecrement.not()) {
                    item.count = item.count + binding.newCount.text.toString().toDouble()
                    viewModel.updateCountForItem(item)
                    notifyAdapterAboutChangeInItem(item)
                    dialog.dismiss()
                } else {
                    if (item.count > binding.newCount.text.toString().toDouble()) {
                        item.count -= binding.newCount.text.toString().toDouble()
                        viewModel.updateCountForItem(item)
                        notifyAdapterAboutChangeInItem(item)
                        dialog.dismiss()
                    }
                }
            }
        }
        dialog.show()
    }


    private fun notifyAdapterAboutChangeInItem(item: Item) {
        adapter.notifyItemChanged(adapter.itemList.indexOf(item))
    }

    private fun notifyAdapterForDeletion(item:Item){
        val index =adapter.itemList.indexOf(item)
        adapter.itemList.removeAt(index)
        adapter.notifyItemRemoved(index)
    }

    private fun switchToItemDetailsFragment(){
        (activity as MainActivity).changeFragment(ItemDetailsFragment::class.java)
    }





}