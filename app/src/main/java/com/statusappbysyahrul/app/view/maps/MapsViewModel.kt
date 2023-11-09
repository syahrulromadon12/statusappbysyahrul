package com.statusappbysyahrul.app.view.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.statusappbysyahrul.app.data.UserRepository
import com.statusappbysyahrul.app.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: UserRepository) : ViewModel() {
    private val _stories = MutableLiveData<List<ListStoryItem?>?>()
    val stories: LiveData<List<ListStoryItem?>?> get() = _stories

    private val _isLoading = MutableLiveData<Boolean>()

    fun getStoriesWithLocation(){
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val storiesResponse = repository.getStoriesWithLocation()
                _stories.value = storiesResponse.listStory
            } catch (e: Exception) {
                _isLoading.value = false
                Log.e(TAG, "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    companion object{
        private const val TAG = "MapsViewModel"
    }
}