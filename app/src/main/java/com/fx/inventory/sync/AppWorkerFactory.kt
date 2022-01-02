package com.fx.inventory.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.repos.CategoryRepository
import com.fx.inventory.data.repos.DocumentRepository
import com.fx.inventory.data.repos.ItemRepository
import retrofit2.Retrofit
import javax.inject.Inject

class AppWorkerFactory @Inject constructor(
    val itemRepository: ItemRepository,
    val categoryRepository: CategoryRepository,
    val documentRepository: DocumentRepository,
    val dataManager: DataManager,
    val retrofit: Retrofit,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncWorker::class.java.name -> {
                SyncWorker(
                    categoryRepository = categoryRepository,
                    dataManager = dataManager,
                    appContext = appContext,
                    workerParams = workerParameters,
                    retrofit = retrofit
                )
            }
            else -> {
                null
            }
        }
    }
}