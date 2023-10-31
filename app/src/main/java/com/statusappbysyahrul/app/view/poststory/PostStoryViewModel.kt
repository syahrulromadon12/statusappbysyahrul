package com.statusappbysyahrul.app.view.poststory

import androidx.lifecycle.ViewModel
import com.statusappbysyahrul.app.data.UserRepository
import java.io.File

class PostStoryViewModel(private val repository: UserRepository) : ViewModel() {
    fun uploadImage(file: File, description: String, lat: Float, lon: Float) = repository.uploadImage(file, description, lat, lon)
}
