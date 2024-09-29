package com.example.videosapp.data.repository

import com.example.videosapp.data.model.VideoError
import com.example.videosapp.data.model.VideoHit
import com.example.videosapp.data.remote.VideoService
import com.example.videosapp.data.room.VideoEntity
import com.example.videosapp.data.room.VideosDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val videoService: VideoService,
    private val videosDao: VideosDao
) : VideoRepository {

    override fun getVideos(): Flow<Result<List<VideoHit>>> = flow {
        val cachedVideos = videosDao.getAllVideos().map { it.toModel() }.sortedBy { it.duration }
        if (cachedVideos.isNotEmpty()) {
            emit(Result.success(cachedVideos))
        }
        try {
            val videos = videoService.getVideos().hits
            videosDao.clearVideos()
            videosDao.addVideos(videos.map { VideoEntity.createEntity(it) })
            emit(Result.success(videos.sortedBy { it.duration }))
        } catch (exception: Throwable) {
            if (cachedVideos.isNotEmpty()) {
                emit(Result.success(cachedVideos))
            } else {
                emit(Result.failure(handleNetworkException(exception)))
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun handleNetworkException(exception: Throwable): VideoError {
        return when (exception) {
            is IOException -> VideoError.NetworkError
            is HttpException -> {
                when (exception.code()) {
                    in 400..499 -> VideoError.ApiError(exception.code(), exception.message())
                    in 500..599 -> VideoError.ServerError
                    else -> VideoError.UnknownError
                }
            }

            else -> VideoError.UnknownError
        }
    }
}
