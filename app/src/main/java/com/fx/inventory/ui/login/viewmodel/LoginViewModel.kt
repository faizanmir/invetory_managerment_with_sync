package com.fx.inventory.ui.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fx.inventory.ui.login.repo.LoginRepository
import com.fx.inventory.ui.login.viewmodel.interfaces.LoginNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(var loginRepository: LoginRepository) : ViewModel() {
    sealed class LoginState {
        object Success : LoginState()
        object Error : LoginState()
        object Initial : LoginState()
    }

    var loginNavigator: LoginNavigator? = null
    private val _uiState = MutableStateFlow<LoginState>(LoginState.Initial)
    val uiState: StateFlow<LoginState> = _uiState


    init {
        getAuthToken()
    }


    fun performLogin(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = loginRepository.performLogin(email, password)
                loginRepository.setAuthToken(response.jwtToken)
                loginRepository.storeEmail(email)
                loginRepository.storePassword(password)
                _uiState.value = LoginState.Success
            } catch (e: Exception) {
                _uiState.value = LoginState.Error
            }
        }
    }

    private fun getAuthToken() {
        viewModelScope.launch(IO) {
            Log.e(TAG, "getAuthToken: ${ loginRepository.getAuthToken().first()}", )
            loginNavigator?.onAuthTokenFound(
                loginRepository.getAuthToken().first().isNullOrBlank().not()
            )
        }
    }


    fun testFunction() {
        loginRepository.testInjection()
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}