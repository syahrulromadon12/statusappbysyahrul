package com.statusappbysyahrul.app.data

import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.gson.Gson
import com.statusappbysyahrul.app.data.api.ApiConfig
import com.statusappbysyahrul.app.data.api.ApiService
import com.statusappbysyahrul.app.data.paging.StoryPagingSource
import com.statusappbysyahrul.app.data.pref.UserModel
import com.statusappbysyahrul.app.data.pref.UserPreference
import com.statusappbysyahrul.app.data.response.AllStoriesResponse
import com.statusappbysyahrul.app.data.response.DetailStoryResponse
import com.statusappbysyahrul.app.data.response.ListStoryItem
import com.statusappbysyahrul.app.data.response.LoginResponse
import com.statusappbysyahrul.app.data.response.SuccessResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File


class UserRepository private constructor(
    private var apiService: ApiService,
    private val userPreference: UserPreference
) {
    suspend fun loginUser(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun getStories(): Flow<PagingData<ListStoryItem>> {
        val user = userPreference.getSession().first()
        val apiService = ApiConfig.getApiService(user.token)
        return Pager(
            config = PagingConfig(
                pageSize = 20
            ),
            pagingSourceFactory = { StoryPagingSource(apiService) }
        ).flow
    }

    suspend fun getDetailStoryById(id: String): DetailStoryResponse {
        val user = userPreference.getSession().first()
        apiService = ApiConfig.getApiService(user.token)
        return apiService.getDetailStory(id)
    }

    suspend fun getStoriesWithLocation(): AllStoriesResponse{
        val user = userPreference.getSession().first()
        apiService = ApiConfig.getApiService(user.token)
        return  apiService.getStoriesWithLocation()
    }

    fun uploadImage(imageFile: File, description: String, lat: Float, lon: Float) = liveData {
        val user = userPreference.getSession().first()
        apiService = ApiConfig.getApiService(user.token)

        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())

        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        try {
            val successResponse = apiService.postStory(multipartBody, requestBody, lat, lon)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, SuccessResponse::class.java)
            emit(errorResponse.message?.let { ResultState.Error(it) })
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService,userPreference)
            }.also { instance = it }
    }
}