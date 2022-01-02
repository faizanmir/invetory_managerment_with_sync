package com.fx.inventory.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.fx.inventory.R
import com.fx.inventory.databinding.ActivityLoginBinding
import com.fx.inventory.ui.login.viewmodel.LoginViewModel
import com.fx.inventory.ui.login.viewmodel.interfaces.LoginNavigator
import com.fx.inventory.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() ,LoginNavigator{
    private val loginViewModel:LoginViewModel by viewModels()
    private lateinit var binding:ActivityLoginBinding

    override fun onStart() {
        loginViewModel.loginNavigator =  this
        super.onStart()
    }

    override fun onDestroy() {
        loginViewModel.loginNavigator =  null
        super.onDestroy()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  DataBindingUtil.setContentView(this, R.layout.activity_login);


        binding.loginButton.setOnClickListener {
            if(binding.email.text.toString().isBlank().not()&&binding.password.text.toString().isBlank().not() ) {
                loginViewModel.performLogin(
                    binding.email.text.toString(),
                    binding.password.text.toString()
                )
            }else{
                binding.email.error = "Field Required"
                binding.password.error = "Field Required"
            }
        }


        lifecycleScope.launchWhenStarted {
         loginViewModel.uiState.collect {
             when(it){
                 LoginViewModel.LoginState.Error ->Log.e(Companion.TAG, "onCreate: $it", )
                 LoginViewModel.LoginState.Initial -> Log.e(Companion.TAG, "onCreate: $it", )
                 LoginViewModel.LoginState.Success -> {
                     onAuthTokenFound(true)
                 }
             }
         }
        }
    }


    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onAuthTokenFound(found: Boolean) {
        if(found){
            val intent  =  Intent(this@LoginActivity,MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }


}