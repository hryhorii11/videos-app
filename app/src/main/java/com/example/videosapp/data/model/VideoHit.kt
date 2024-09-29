package com.example.videosapp.data.model

data class VideoHit(
    val id:Long,
    val type: String,
    val tags: String,
    val duration: Int,
    val videos:VideoFormats
)
