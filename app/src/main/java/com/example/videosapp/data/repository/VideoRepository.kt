package com.example.videosapp.data.repository

import com.example.videosapp.data.model.VideoHit
import kotlinx.coroutines.flow.Flow


interface VideoRepository {

    fun getVideos(): Flow<Result<List<VideoHit>>>
}