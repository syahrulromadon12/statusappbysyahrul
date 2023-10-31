package com.statusappbysyahrul.app.view.auth.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.statusappbysyahrul.app.data.api.ApiConfig
import com.statusappbysyahrul.app.data.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupViewModel : ViewModel() {
    private val apiService: ApiService = ApiConfig.getApiService(this.toString())

    private val _registrationResult = MutableLiveData<Boolean>()
    val registrationResult: LiveData<Boolean> = _registrationResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                _isLoading.postValue(true)

                val response = apiService.register(name, email, password)
                val registrationSuccess = response.error == false

                withContext(Dispatchers.Main) {
                    _registrationResult.value = registrationSuccess
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _registrationResult.value = false
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}