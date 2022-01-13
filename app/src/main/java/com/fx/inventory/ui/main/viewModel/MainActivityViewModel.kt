package com.fx.inventory.ui.main.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fx.inventory.data.models.Category
import com.fx.inventory.data.models.Document
import com.fx.inventory.data.models.Item
import com.fx.inventory.data.repos.CategoryRepository
import com.fx.inventory.data.repos.DocumentRepository
import com.fx.inventory.data.repos.ItemRepository
import com.fx.inventory.ui.main.viewModel.interfaces.CategoryActionHandler
import com.fx.inventory.ui.main.viewModel.interfaces.ItemActionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val itemRepository: ItemRepository,
    private val documentRepository: DocumentRepository
) :
    ViewModel() {
    //interfaces
    lateinit var categoryActionHandler: CategoryActionHandler
    lateinit var itemViewActionHandler: ItemActionHandler

    //livedata
    var categoryListLiveData = MutableLiveData<List<Category>>(arrayListOf())
    var itemListLiveData = MutableLiveData<List<Item>>(arrayListOf())
    var documentListLiveData = MutableLiveData<MutableList<Document>>(arrayListOf())

    //state
    var category: Category? = null
    var item: Item? = null

    fun saveCategory(categoryName: String) {
        viewModelScope.launch(IO) {
            categoryRepository.storeCategory(
                Category(
                    categoryName = categoryName,
                    requiresAction = false
                )
            )
            getAllCategories()
        }
    }


    fun getAllCategories() {
        viewModelScope.launch(IO) {
            categoryListLiveData.postValue(categoryRepository.getAllCategories())
        }
    }


    fun updateCategory(category: Category) {
        viewModelScope.launch(IO) {
            categoryRepository.updateCategory(category)
            categoryRepository.setUpdated(category.cid, true)

            getAllCategories()
        }
    }


    fun setCategoryDeleted(cid: Int) {
        viewModelScope.launch(IO) {
            categoryRepository.setDeleted(cid)
            itemRepository.setCategoryDeleted(cid)
            getAllCategories()
        }

    }


    fun saveItemForCategory(name: String, rate: Double, count: Double) {
        viewModelScope.launch(IO) {
            itemRepository.addItem(
                Item(
                    name = name,
                    rate = rate,
                    count = count,
                    catServerId = category?.serverId!!,
                    localCategoryId = category?.cid!!,
                    categoryHasSynced = category?.hasSynced!!
                )
            )
            getAllItemsForCategory()
        }
    }

    fun getAllItemsForCategory() {
        viewModelScope.launch(IO) {
            itemListLiveData.postValue(itemRepository.getAllItemsForCategory(category?.cid!!))
        }
    }

    fun setItemDeleted(item: Item) {
        viewModelScope.launch(IO) {
            itemRepository.setItemDeleted(item.itemId)
        }
        // getAllItemsForCategory()
    }

    fun updateItem(item: Item) {
        viewModelScope.launch(IO) {
            itemRepository.updateItem(item);
            itemRepository.setItemUpdated(item.itemId, true)
        }
    }


    fun updateCountForItem(item: Item) {
        viewModelScope.launch(IO) {
            itemRepository.updateItemCount(item.count, item.itemId)
            itemRepository.setItemUpdated(item.itemId, true)
        }
    }


    fun getAllDocumentsForItem() {
        documentListLiveData.value?.clear()
        viewModelScope.launch(IO) {
            val docList = documentRepository.getAllDocumentsForItem(item?.itemId!!)
            val list = mutableListOf<Document>()
            list.addAll(docList)
            withContext(Main) {
                documentListLiveData.value = list
            }
        }
    }


    private fun saveDocumentForItem(uri: Uri) {
        viewModelScope.launch(IO) {
            if (item != null) {
                val document = Document(
                    localItemId = item?.itemId!!,
                    itemServedId = item?.itemServedId!!,
                    filePath = uri.toString(),
                )
                documentRepository.addDocument(document)
                getAllDocumentsForItem()
            }
        }

    }


    fun deleteDocument(document: Document) {
        viewModelScope.launch(IO) {
            documentRepository.markItemForDeletion(document.id, true);
            getAllDocumentsForItem()
        }

    }


    fun onAddCategoryClick() {
        categoryActionHandler.onAddCategoryPressed()
    }

    fun onAddItemClicked() {
        itemViewActionHandler.addItemClicked()
    }

    fun saveUriForItem(uris: ArrayList<Uri>) {
        uris.forEach {
            saveDocumentForItem(it)
        }
    }

    fun performInitialSyncIfRequired() {
        viewModelScope.launch(IO) {
            if (categoryRepository.getAllCategories().isEmpty()) {
                //perform fetch and populate local db
                //perform fetch for category and get server category ids for each category
                //perform fetch for each category
                //document fetch for adding image urls into the db
                categoryRepository.fetchCategoriesOnline().categories.forEach { cat ->
                    categoryRepository.storeCategory(
                        Category(
                            serverId = cat.serverId,
                            categoryName = cat.categoryName,
                            requiresAction = cat.requiresAction,
                            hasSynced = true
                        )
                    );

                    val savedCat = categoryRepository.getAllCategories()
                        .first { category -> category.serverId == cat.serverId }

                    itemRepository.fetchItemsForCategory(cat.serverId).catItems.forEach { item->
                        val itemToSave  = Item(
                            name = item.name,
                            rate = item.rate,
                            count = item.count,
                            categoryHasSynced = true,
                            catServerId = cat.serverId,
                            localCategoryId =savedCat.cid,
                            hasSynced = true,
                            itemServedId = item.itemServedId,
                        )

                        itemRepository.addItem(itemToSave)

                        val savedItem  = itemRepository.getAllItems()
                            .first { i -> i.itemServedId == item.itemServedId }

                        if(item.files.isNotEmpty()){
                            item.files.forEach {
                                val doc = Document(
                                    localItemId = savedItem.itemId,
                                    itemServedId = savedItem.itemServedId,
                                    hasSynced = true,
                                    serverUrl = it.serverUrl,
                                    serverId = it.serverId,
                                    filePath = ""
                                )
                                Log.e(TAG, "performInitialSyncIfRequired: Inserting doc into db....", )
                                documentRepository.addDocument(doc)
                            }
                        }
                    }

                }
                getAllCategories()
            }
        }
    }


    companion object {
        private const val TAG = "MainActivityViewModel"
    }

}