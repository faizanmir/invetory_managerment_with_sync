package com.fx.inventory.ui.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fx.inventory.ui.login.repo.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(var loginRepository: LoginRepository,):ViewModel() {
    sealed class LoginState{
        object Success:LoginState()
        object Error:LoginState()
        object Initial:LoginState()
    }

    private val _uiState = MutableStateFlow<LoginState>(LoginState.Initial)
    val uiState: StateFlow<LoginState> = _uiState


    fun performLogin(){
        viewModelScope.launch {
            try {
                val response = loginRepository.performLogin("faizanmir009@gmail.com", "12345")
                loginRepository.setAuthToken(response.jwtToken);
                _uiState.value = LoginState.Success
            } catch (e:Exception){
                _uiState.value =  LoginState.Error
            }
        }
    }

    fun getAuthToken(){
        viewModelScope.launch {
            loginRepository.getAuthToken().collect {
                Log.e(TAG, "getAuthToken: $it", )
            }
        }
    }


    fun testFunction(){
        loginRepository.testInjection();
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}