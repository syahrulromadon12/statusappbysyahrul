package com.statusappbysyahrul.app.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.statusappbysyahrul.app.data.UserRepository
import com.statusappbysyahrul.app.data.pref.UserModel
import com.statusappbysyahrul.app.data.response.ListStoryItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _stories = MutableLiveData<PagingData<ListStoryItem>>()
    val stories: LiveData<PagingData<ListStoryItem>> get() = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        getStories()
    }

    fun getStories() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val flow = repository.getStories().cachedIn(viewModelScope)
                flow.collectLatest { data ->
                    _stories.value = data
                }
            } catch (e: Exception) {
                _isLoading.value = false
                Log.e(TAG, "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
