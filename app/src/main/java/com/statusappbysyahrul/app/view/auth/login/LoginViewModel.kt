package com.statusappbysyahrul.app.view.auth.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.statusappbysyahrul.app.data.UserRepository
import com.statusappbysyahrul.app.data.pref.UserModel
import kotlinx.coroutines.launch


class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.loginUser(email, password)
                if (response.error == false) {
                    val token = response.loginResult?.token ?: ""
                    val name = response.loginResult?.name ?: ""
                    repository.saveSession(UserModel(name, token, isLogin = true))
                    _loginResult.value = true
                    _isLoading.value = true
                } else {
                    _loginResult.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = true
                _loginResult.value = false
                Log.e(TAG, "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}
