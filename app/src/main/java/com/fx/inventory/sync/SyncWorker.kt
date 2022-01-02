package com.fx.inventory.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.models.Category
import com.fx.inventory.data.repos.CategoryRepository
import com.fx.inventory.data.repos.DocumentRepository
import com.fx.inventory.data.repos.ItemRepository
import com.google.gson.JsonObject
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Retrofit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    val dataManager: DataManager,
    val categoryRepository: CategoryRepository,
    val itemRepository: ItemRepository,
    val documentRepository: DocumentRepository,
    val retrofit: Retrofit
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "SyncWorker"
    }

    private var syncApi: SyncApi = retrofit.create(SyncApi::class.java)


    override suspend fun doWork(): Result {
        val authToken = dataManager.getAuthToken().first()

        deleteUnSyncedCategoryMarkedForDeletion()

        if (authToken != null) {
            performCategorySync()
            performItemSync()
        }

        return Result.success()

    }

    private suspend fun performCategorySync() {
        categoryRepository.getCategoriesForSyncStatus(false).forEach {
            uploadCategory(it)
        }

        categoryRepository.getSyncedUpdatedCategories(hasSynced = true, updated = true)
            .forEach {
                updateCategory(it)
            }

        categoryRepository.getSyncedDeletedCategories(hasSynced = true, deleted = true)
            .forEach {
                deleteCategory(it)
            }
    }

    private suspend fun performItemSync() {
        uploadItem()
        updateItem()
        deleteItemsFromServer()
        deleteItemsLocally()
    }


    private suspend fun uploadItem() {
        itemRepository.getItemsForSyncedCategory(true).forEach {
            if (it.deleted.not()) {
                val response = syncApi.uploadItem(
                    headers = formHeaderMap(),
                    cid = it.catServerId,
                    name = RequestBody.create(MediaType.parse("text/plain"), it.name),
                    count = RequestBody.create(MediaType.parse("text/plain"), it.count.toString()),
                    rate = RequestBody.create(MediaType.parse("text/plain"), it.rate.toString())
                )
                itemRepository.updateItemServerId(response.id!!, it.itemId)
            }
        }
    }


    private suspend fun updateItem() {
        itemRepository.getAllItems()
            .filter { it.catDeleted.not() && it.deleted.not() && it.updated }
            .forEach {
                val response = syncApi.updateItem(
                    headers = formHeaderMap(),
                    cid = it.catServerId,
                    itemId = it.catServerId,
                    item = JsonObject().apply {
                        addProperty("name", it.name)
                        addProperty("count", it.count)
                        addProperty("rate", it.rate)
                    })
                itemRepository.setItemUpdated(it.itemId, false)
            }
    }

    private suspend fun deleteItemsFromServer() {
        itemRepository.getItemsWhereCategoryDeleteStatusAndSyncStatus(
            catDeleted = true,
            hasSynced = true
        ).forEach {
            syncApi.deleteItem(
                headers = formHeaderMap(),
                cid = it.catServerId,
                itemId = it.itemServedId
            )
            itemRepository.deleteItem(item = it)
        }
    }

    private suspend fun deleteItemsLocally() {
        itemRepository.getItemsWhereCategoryDeleteStatusAndSyncStatus(
            catDeleted = true,
            hasSynced = false
        ).forEach {
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
        val response = syncApi.deleteCategory(headers = formHeaderMap(), category.cid)
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
        this["Authorization"] = "${dataManager.getAuthToken().first()}"
    }


}