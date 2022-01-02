package com.fx.inventory.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.models.Category
import com.fx.inventory.data.repos.CategoryRepository
import com.fx.inventory.data.repos.DocumentRepository
import com.fx.inventory.data.repos.ItemRepository
import com.fx.inventory.ui.login.repo.LoginRepository
import com.google.gson.JsonObject
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    val dataManager: DataManager,
    val categoryRepository: CategoryRepository,
    val itemRepository: ItemRepository,
    val loginRepository: LoginRepository,
    val documentRepository: DocumentRepository,
    val retrofit: Retrofit
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "SyncWorker"
    }

    private var syncApi: SyncApi = retrofit.create(SyncApi::class.java)


    override suspend fun doWork(): Result {
        val authToken = dataManager.getAuthToken().first()
        Log.d(TAG, "doWork: Sync started ....")
        if (authToken != null) {
            performSync()
        } else {
            performLogin()
            performSync()
        }

        return Result.success()

    }


    private suspend fun performLogin() {
        if (dataManager.retrieveEmail().first().isNullOrBlank()
                .not() && dataManager.retrievePassword().first().isNullOrBlank().not()
        ) {
            val response = loginRepository.performLogin(
                email = dataManager.retrieveEmail().first()!!,
                password = dataManager.retrievePassword().first()!!
            )
            dataManager.setAuthToken(response.jwtToken)
        }
    }

    private suspend fun performSync() {
        deleteUnSyncedCategoryMarkedForDeletion()
        performCategorySync()
        performItemSync()
    }


    private suspend fun performCategorySync() {
        categoryRepository.getCategoriesForSyncStatus(false).forEach {
            Log.e(TAG, "performCategorySync1: ${it}")
            uploadCategory(it)
        }
        categoryRepository.getSyncedUpdatedCategories(hasSynced = true, updated = true)
            .forEach {
                Log.e(TAG, "performCategorySync2: ${it}")
                if(it.deleted.not()){
                    updateCategory(it)
                }

            }

        categoryRepository.getSyncedDeletedCategories(hasSynced = true, deleted = true)
            .forEach {
                Log.e(TAG, "performCategorySync3: ${it}")
                deleteCategory(it)
            }
    }

    private suspend fun performItemSync() {
        deleteItemsLocally()
        uploadItem()
        updateItem()
        deleteItemsMarkedForDeletion()
        //deleteItemsFromServer()
        //deleteItemsLocally()
    }

    private suspend fun deleteItemsMarkedForDeletion(){
        itemRepository.getAllItems().filter {
            it.deleted && it.hasSynced
        }.forEach {
            syncApi.deleteItem(
                headers = formHeaderMap(),
                cid = it.catServerId,
                itemId = it.itemServedId)
            itemRepository.deleteItem(it)
        }

    }



    private suspend fun uploadItem() {
        itemRepository.getAllItems().filter { it.hasSynced.not() && it.deleted.not()&& it.categoryHasSynced }.forEach {
                val response = syncApi.uploadItem(
                    headers = formHeaderMap(),
                    cid = it.catServerId,
                    body = JsonObject().also { json->
                        json.addProperty("name",it.name)
                        json.addProperty("count",it.count)
                        json.addProperty("rate",it.rate)
                    }
                )
                itemRepository.setItemSynced(true,it.itemId)
                itemRepository.updateItemServerId(response.id!!, it.itemId)

        }
    }


    private suspend fun updateItem() {
        itemRepository.getAllItems()
            .filter { it.catDeleted.not() && it.deleted.not() && it.updated }
            .forEach {

                val response = syncApi.updateItem(
                    headers = formHeaderMap(),
                    cid = it.catServerId,
                    itemId = it.itemServedId,
                    item = JsonObject().apply {
                        addProperty("name", it.name)
                        addProperty("count", it.count)
                        addProperty("rate", it.rate)
                    })
                //itemRepository.updateItemServerId(itemServerId = response.id!!,it.itemId)
                itemRepository.setItemUpdated(it.itemId, false)
            }
    }


    private suspend fun deleteItemsLocally() {
        itemRepository.getAllItems().filter {
            it.deleted && it.hasSynced.not()
        }.forEach {
            itemRepository.deleteItem(it)
        }
    }

    private suspend fun uploadCategory(category: Category) {
        val response = syncApi.uploadCategory(headers = formHeaderMap(), JsonObject().apply {
            addProperty("categoryName", category.categoryName)
            addProperty("requiresAction", false)
        })
        categoryRepository.setSynced(category.cid, true)
        itemRepository.updateItemsForCategorySyncStatus(true, category.cid)
        categoryRepository.updateServerId(categoryServerId = response.id!!, cid = category.cid)
    }


    private suspend fun updateCategory(category: Category) {
        syncApi.updateCategory(
            headers = formHeaderMap(),
            catId = category.serverId,
            JsonObject().apply {
                addProperty("categoryName", category.categoryName)
                addProperty("requiresAction", category.requiresAction)
            })
        categoryRepository.setUpdated(category.cid, false)
        categoryRepository.setSynced(cid = category.cid, true)
    }

    private suspend fun deleteCategory(category: Category) {
        val response = syncApi.deleteCategory(headers = formHeaderMap(), category.serverId)
        if (response.success) {
            categoryRepository.deleteCategory(category)
        }
    }

    private suspend fun deleteUnSyncedCategoryMarkedForDeletion() {
        categoryRepository.getSyncedDeletedCategories(hasSynced = false, deleted = true)
            .forEach {
                categoryRepository.deleteCategory(it)
            }
    }


    private suspend fun formHeaderMap(): Map<String, String> = HashMap<String, String>().apply {
        //this["Accept"] = "application/json"
        this["Content-Type"] = "application/json"
        this["Authorization"] = "Bearer ${dataManager.getAuthToken().first()}"
    }

    private fun toRequestBody(value: String): RequestBody {
        return MultipartBody.create(MultipartBody.ALTERNATIVE, value)
    }


}