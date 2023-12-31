package com.statusappbysyahrul.app.data.api

import com.statusappbysyahrul.app.data.response.AllStoriesResponse
import com.statusappbysyahrul.app.data.response.DetailStoryResponse
import com.statusappbysyahrul.app.data.response.LoginResponse
import com.statusappbysyahrul.app.data.response.SuccessResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): SuccessResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories (
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): AllStoriesResponse

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Path("id") id: String
    ): DetailStoryResponse

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Part file  : MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat : Float,
        @Part("lon") lon : Float,
    ): SuccessResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location : Int = 1,
    ): AllStoriesResponse
}