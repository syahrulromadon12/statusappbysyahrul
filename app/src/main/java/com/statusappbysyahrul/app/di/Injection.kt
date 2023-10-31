package com.statusappbysyahrul.app.di

import android.content.Context
import com.statusappbysyahrul.app.data.UserRepository
import com.statusappbysyahrul.app.data.api.ApiConfig
import com.statusappbysyahrul.app.data.pref.UserPreference
import com.statusappbysyahrul.app.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService( user.token)
        return UserRepository.getInstance(apiService, pref)
    }
}
