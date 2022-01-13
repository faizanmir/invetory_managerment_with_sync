package com.fx.inventory.sync

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.models.Category
import com.fx.inventory.data.models.Document
import com.fx.inventory.data.repos.CategoryRepository
import com.fx.inventory.data.repos.DocumentRepository
import com.fx.inventory.data.repos.ItemRepository
import com.fx.inventory.ui.login.repo.LoginRepository
import com.google.gson.JsonObject
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import retrofit2.Retrofit

import java.io.*


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
        performDocumentSync()
        deleteFilesMarkedForDeletion()
    }


    private suspend fun performCategorySync() {
        categoryRepository.getCategoriesForSyncStatus(false).forEach {
            uploadCategory(it)
        }
        categoryRepository.getSyncedUpdatedCategories(hasSynced = true, updated = true)
            .forEach {
                if (it.deleted.not()) {
                    updateCategory(it)
                }

            }

        categoryRepository.getSyncedDeletedCategories(hasSynced = true, deleted = true)
            .forEach {
                deleteCategory(it)
            }
    }

    private suspend fun performItemSync() {
        deleteItemsLocally()
        uploadItem()
        updateItem()
        deleteItemsMarkedForDeletion()
    }

    private suspend fun deleteItemsMarkedForDeletion() {
        itemRepository.getAllItems().filter {
            it.deleted && it.hasSynced
        }.forEach {
            syncApi.deleteItem(
                headers = formHeaderMap(),
                cid = it.catServerId,
                itemId = it.itemServedId
            )
            itemRepository.deleteItem(it)
        }

    }


    private suspend fun uploadItem() {
        itemRepository.getAllItems()
            .filter { it.hasSynced.not() && it.deleted.not() && it.categoryHasSynced }.forEach {
                val response = syncApi.uploadItem(
                    headers = formHeaderMap(),
                    cid = it.catServerId,
                    body = JsonObject().also { json ->
                        json.addProperty("name", it.name)
                        json.addProperty("count", it.count)
                        json.addProperty("rate", it.rate)
                    }
                )
                itemRepository.setItemSynced(true, it.itemId)
                itemRepository.updateItemServerId(response.id!!, it.itemId)

            }
    }


    private suspend fun updateItem() {
        itemRepository.getAllItems()
            .filter { it.catDeleted.not() && it.deleted.not() && it.updated }
            .forEach {

                syncApi.updateItem(
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
        try {
            val response = syncApi.deleteCategory(headers = formHeaderMap(), category.serverId)
            if (response.success) {
               deleteCategoryLocally(category)
            }
        }catch (e:Exception){
           deleteCategoryLocally(category)
        }
    }

    private suspend fun deleteCategoryLocally(category: Category){
        itemRepository.getAllItemsForCategory(category.cid).forEach {
            documentRepository.deleteDocsForItem(it.itemId);
        }
        itemRepository.deleteItemsWithCatId(category.cid)
        categoryRepository.deleteCategory(category)
    }


    private suspend fun deleteUnSyncedCategoryMarkedForDeletion() {
        categoryRepository.getSyncedDeletedCategories(hasSynced = false, deleted = true)
            .forEach { category ->
                itemRepository.getAllItemsForCategory(category.cid).forEach {
                    documentRepository.deleteDocsForItem(it.itemId);
                }
                itemRepository.deleteItemsWithCatId(category.cid)
                categoryRepository.deleteCategory(category)
            }
    }


    private suspend fun performDocumentSync() {
        documentRepository.getAllDocuments().filter {
            it.deleted.not() && it.hasSynced.not()
        }.forEach {
            val item = itemRepository.getItem(it.localItemId)
            val file =
                convertImageFileToBase64(File(getRealPathFromURI(Uri.parse(it.filePath))))?.replace(
                    "\n",
                    ""
                )!!
            if (item.hasSynced) {
                documentRepository.updateItemServerId(it.id, item.itemServedId)
                val response = syncApi.addFileToItem(
                    formHeaderMap(),
                    item.itemServedId,
                    file
                )
                documentRepository.updateParams(
                    id = it.id,
                    response.id?.toInt()!!,
                    response.url!!,
                    true
                )
            }
        }

    }


    private suspend fun deleteFilesMarkedForDeletion() {
        documentRepository.getAllDocuments().forEach {
            if (it.hasSynced.not() && it.deleted) {
                performLocalDeletion(it)
            } else if (it.hasSynced && it.deleted) {
                try {
                    syncApi.deleteFileFromItem(formHeaderMap(), it.itemServedId, it.serverId)
                    performLocalDeletion(it)
                } catch (e: Exception) {
                    performLocalDeletion(it)
                }
            }
        }
    }

    private suspend fun performLocalDeletion(document: Document) {
        val file = File(getRealPathFromURI(Uri.parse(document.filePath)))
        if (file.exists()) {
            file.delete()
        }
        documentRepository.deleteDocument(document)
    }


    private suspend fun formHeaderMap(): Map<String, String> = HashMap<String, String>().apply {
        //this["Accept"] = "application/json"
        this["Content-Type"] = "application/json"
        this["Authorization"] = "Bearer ${dataManager.getAuthToken().first()}"
    }


    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor? =
            applicationContext.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    private fun convertImageFileToBase64(imgFile: File): String? {
        val inputStream: InputStream =
            FileInputStream(imgFile) // You can get an inputStream using any I/O API

        val bytes: ByteArray
        val buffer = ByteArray(8192)
        var bytesRead: Int
        val output = ByteArrayOutputStream()

        try {
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        bytes = output.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }


}