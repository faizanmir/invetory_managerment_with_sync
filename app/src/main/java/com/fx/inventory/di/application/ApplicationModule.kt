package com.fx.inventory.di.application

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.fx.inventory.constants.Constants
import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.datamanager.DataManagerImpl
import com.fx.inventory.data.db.AppDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
 class ApplicationModule {
    @Provides
    @Singleton
    fun providesDataManager(@ApplicationContext context: Context):DataManager{
        return DataManagerImpl(context);
    }

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context):AppDb{
        return Room.databaseBuilder(
            context,
            AppDb::class.java, "inventory-database"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Singleton
    @Provides
    fun provideOkHttp(): OkHttpClient {
        return OkHttpClient()
            .newBuilder()
            .connectTimeout(Duration.ofMinutes(3))
            .build()
    }


}