package com.fx.inventory.ui.main.fragments.item_details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.fx.inventory.databinding.FragmentItemDetailsBinding
import com.fx.inventory.ui.main.MainActivity
import com.fx.inventory.ui.main.fragments.camera.CameraFragment
import com.fx.inventory.ui.main.viewModel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemDetailsFragment:Fragment() {
    
    companion object{
        private const val TAG = "ItemDetailsFragment"
    }

    private val viewModel: MainActivityViewModel by activityViewModels()
    lateinit var binding:FragmentItemDetailsBinding
    lateinit var documentAdapter: DocumentGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  FragmentItemDetailsBinding.inflate(LayoutInflater.from(context))
        binding.vm  =  viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideNoDocumentsTextView()

        if(viewModel.item!=null) {
            viewModel.getAllDocumentsForItem()
        }

        documentAdapter = DocumentGridAdapter {
            viewModel.deleteDocument(document = it)
        }

        binding.documentRecyclerView.adapter =  documentAdapter

        viewModel.documentListLiveData.observe(viewLifecycleOwner,{
            if(it.isNotEmpty()){
                hideNoDocumentsTextView()
                documentAdapter.updateData(it)
            }else{
                showNoDocumentsTextView()
            }
        })

        binding.addMorePhotos.setOnClickListener {
            (activity as MainActivity).changeFragment(CameraFragment::class.java)
        }

        binding.documentRecyclerView.layoutManager = GridLayoutManager(context,3)

    }


    private fun hideNoDocumentsTextView(){
        binding.noImagesPresent.visibility =View.GONE
    }

    private fun showNoDocumentsTextView(){
        binding.noImagesPresent.visibility =View.VISIBLE
    }



}