package com.fx.inventory.di.login

import com.fx.inventory.data.datamanager.DataManager
import com.fx.inventory.data.db.AppDb
import com.fx.inventory.data.login.web.LoginWebService
import com.fx.inventory.ui.login.repo.LoginRepository
import com.fx.inventory.ui.login.repo.LoginRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
class LoginModule {

    @Provides
    fun provideLoginService(retrofit: Retrofit):LoginWebService{
        return LoginWebService(retrofit)
    }

    @Provides
     fun provideLoginRepository(dataManager:DataManager,loginWebService: LoginWebService,db:AppDb):LoginRepository{
         return LoginRepositoryImpl(dataManager,db,loginWebService);
     }



}