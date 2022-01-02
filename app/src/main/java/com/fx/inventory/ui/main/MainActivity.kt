package com.fx.inventory.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.work.*
import com.fx.inventory.R
import com.fx.inventory.sync.SyncWorker
import com.fx.inventory.ui.main.fragments.category.CategoryFragment
import com.fx.inventory.ui.main.fragments.camera.CameraFragment
import com.fx.inventory.ui.main.fragments.item_details.ItemDetailsFragment
import com.fx.inventory.ui.main.fragments.item.ItemFragment
import com.fx.inventory.ui.main.viewModel.interfaces.FragmentListener
import com.fx.inventory.ui.main.viewModel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FragmentListener {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    companion object {
        private const val TAG = "MainActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        changeFragment(CategoryFragment::class.java)
        startSyncService()
    }


    override fun changeFragment(clazz: Class<*>) {
        when {
            clazz.isAssignableFrom(CategoryFragment::class.java) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragement_view, CategoryFragment.newInstance())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack("category")
                    .commit()
            }
            clazz.isAssignableFrom(ItemFragment::class.java) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragement_view, ItemFragment.newInstance())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .addToBackStack("items")
                    .commit()
            }
            clazz.isAssignableFrom(CameraFragment::class.java) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragement_view, CameraFragment.newInstance())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack("camera")
                    .commit()
            }

            clazz.isAssignableFrom(ItemDetailsFragment::class.java) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragement_view, ItemDetailsFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack("item-details")
                    .commit()
            }


        }
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach {
            when (it) {
                is ItemFragment -> {
                    changeFragment(CategoryFragment::class.java)
                    mainActivityViewModel.item = null
                }
                is CameraFragment -> {
                    changeFragment(ItemFragment::class.java)
                }
                is CategoryFragment -> {
                    supportFragmentManager.popBackStack(
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    super.onBackPressed()
                }
                is ItemDetailsFragment -> {
                    changeFragment(ItemFragment::class.java)
                }
            }
        }
    }

    private fun startSyncService() {

        val workManager = WorkManager.getInstance(applicationContext)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val testPeriodicWork = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()


        workManager.enqueueUniquePeriodicWork(
            "test",
            ExistingPeriodicWorkPolicy.REPLACE,
            testPeriodicWork
        )
    }

}