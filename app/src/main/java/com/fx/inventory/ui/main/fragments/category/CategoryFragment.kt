package com.fx.inventory.ui.main.fragments.category

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.fx.inventory.R
import com.fx.inventory.data.models.Category
import com.fx.inventory.databinding.DialogAddNewCategoryBinding
import com.fx.inventory.databinding.FragmentCategoryBinding
import com.fx.inventory.ui.main.MainActivity
import com.fx.inventory.ui.main.fragments.item.ItemFragment
import com.fx.inventory.ui.main.viewModel.MainActivityViewModel
import com.fx.inventory.ui.main.viewModel.interfaces.CategoryActionHandler
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CategoryFragment : Fragment(), CategoryActionHandler {

    companion object {
        private const val TAG = "CategoryFragment"
        @JvmStatic
        fun newInstance():CategoryFragment {
            return CategoryFragment()
        }

    }

    private val viewModel: MainActivityViewModel by activityViewModels()

    lateinit var fragmentCategoryBinding: FragmentCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCategoryBinding = FragmentCategoryBinding.inflate(LayoutInflater.from(activity))
        fragmentCategoryBinding.vm = viewModel
        viewModel.categoryActionHandler = this
        return fragmentCategoryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Categories"
        viewModel.getAllCategories()

        fragmentCategoryBinding.noCategoriesPresentTextView.visibility = View.INVISIBLE

        val adapter = CategoryRecyclerViewAdapter(
            onCategoryClicked = {
                viewModel.category =  it;
                Log.e(TAG, "onViewCreated: ${it}", )
                (activity as MainActivity).changeFragment(ItemFragment::class.java)
            },
            onItemDelete = {
                   viewModel.setCategoryDeleted(it)
            },
            onItemEditPressed = {
                showDialog(it)
            }
        )

        fragmentCategoryBinding.categoryRv.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(
            fragmentCategoryBinding.categoryRv.context,
            RecyclerView.VERTICAL
        )
        fragmentCategoryBinding.categoryRv.addItemDecoration(dividerItemDecoration)

        viewModel.categoryListLiveData.observe(viewLifecycleOwner, {
            Log.e(TAG, "onViewCreated: $it")
            if (it.isNotEmpty()) {
                fragmentCategoryBinding.categoryRv.visibility= View.VISIBLE
                fragmentCategoryBinding.noCategoriesPresentTextView.visibility = View.INVISIBLE
                adapter.updateCategoryList(it)
            } else {
                fragmentCategoryBinding.noCategoriesPresentTextView.text =
                    getString(R.string.no_category_present)
                fragmentCategoryBinding.noCategoriesPresentTextView.visibility = View.VISIBLE
                fragmentCategoryBinding.categoryRv.visibility= View.INVISIBLE
            }
        })




    }




    private fun showDialog(category:Category?) {
        val dialogBuilder = AlertDialog.Builder(activity)
        val dialogBinding = DialogAddNewCategoryBinding.inflate(LayoutInflater.from(activity))
        dialogBuilder.setView(dialogBinding.root)
        dialogBinding.vm = viewModel
        val dialog = dialogBuilder.create()

        if(category!=null){
            dialogBinding.categoryNameTextView.setText( category.categoryName)
        }

        dialogBinding.addCategory.setOnClickListener {
            if (dialogBinding.categoryNameTextView.text!!.isNotEmpty()) {
                if(category==null) {
                    viewModel.saveCategory(
                        dialogBinding.categoryNameTextView.text.toString().trim()
                    )
                }else {
                    category.categoryName  = dialogBinding.categoryNameTextView.text.toString().trim();
                    viewModel.updateCategory(category)
                }
                dialog.dismiss()
            } else {
                dialogBinding.categoryNameTextView.error = "Category name cannot be empty"
            }
        }
        dialog.show()
    }



    override fun onItemClicked(id: Int) {

    }



    override fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    override fun onAddCategoryPressed() {
        showDialog(null)
    }
}