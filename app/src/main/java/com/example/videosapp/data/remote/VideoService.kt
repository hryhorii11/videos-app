package com.example.videosapp.data.remote

import com.example.videosapp.data.model.VideoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoService {
    @GET("videos/")
    suspend fun getVideos(
        @Query("key") apiKey: String = KEY,
        @Query("video_type") videoType: String = "all",
        @Query("order") order: String = "popular",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): VideoResponse

    companion object {
        const val BASE_URL = "https://pixabay.com/api/"
        const val KEY = "46217381-c36964e97f2aadeb5d611dda6"
    }
}
