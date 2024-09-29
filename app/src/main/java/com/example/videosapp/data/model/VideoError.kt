package com.example.videosapp.data.model

sealed class VideoError : Exception() {
    data object NetworkError : VideoError()
    data object ServerError : VideoError()
    data object UnknownError : VideoError()
    data class ApiError(val code: Int, override val message: String) : VideoError()
}