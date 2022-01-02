package com.fx.inventory.ui.login

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fx.inventory.R
import com.fx.inventory.ui.login.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val loginViewModel:LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel.testFunction();
        loginViewModel.performLogin();
        loginViewModel.getAuthToken()

        lifecycleScope.launchWhenStarted {
         loginViewModel.uiState.collect {
             when(it){
                 LoginViewModel.LoginState.Error ->Log.e(Companion.TAG, "onCreate: $it", )
                 LoginViewModel.LoginState.Initial -> Log.e(Companion.TAG, "onCreate: $it", )
                 LoginViewModel.LoginState.Success -> Log.e(Companion.TAG, "onCreate: $it", )
             }
         }
        }
    }


    companion object {
        private const val TAG = "LoginActivity"
    }
}