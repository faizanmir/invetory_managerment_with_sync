package com.fx.inventory.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.repos.CategoryRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.Retrofit

@HiltWorker
class SyncWorker@AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    val dataManager: DataManager,
    val categoryRepository: CategoryRepository,
    val retrofit: Retrofit
   ):CoroutineWorker(appContext,workerParams)
{

    companion object{
        private const val TAG = "SyncWorker"
    }
    override suspend fun doWork(): Result {
        Log.e(TAG, "doWork:", )
        return Result.success()
    }
}