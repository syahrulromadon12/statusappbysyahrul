package com.statusappbysyahrul.app.view.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.statusappbysyahrul.app.data.UserRepository
import com.statusappbysyahrul.app.data.response.DetailStoryResponse
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: UserRepository) : ViewModel() {

    private val _detailStory = MutableLiveData<DetailStoryResponse>()
    val detailStory: LiveData<DetailStoryResponse> get() = _detailStory

    fun getDetailStoryById(id: String) {
        viewModelScope.launch {
            try {
                val response = repository.getDetailStoryById(id)
                _detailStory.value = response
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Data is not loaded")
            }
        }
    }
}
