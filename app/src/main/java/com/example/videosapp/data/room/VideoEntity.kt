package com.example.videosapp.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.videosapp.data.model.VideoFormats
import com.example.videosapp.data.model.VideoHit
import com.example.videosapp.data.model.VideoInfo

@Entity(
    tableName = "videos"
)
data class VideoEntity(
    @PrimaryKey
    val id: Long,
    val tags: String,
    val type: String,
    val duration: Int,
    val thumbNail: String?,
    val videoUrl: String,
) {
    fun toModel() = VideoHit(
        id,
        type,
        tags,
        duration,
        VideoFormats(
            VideoInfo(videoUrl, thumbNail)
        )
    )
    companion object {
        fun createEntity(videoHit: VideoHit) = VideoEntity(
            videoHit.id,
            videoHit.tags,
            videoHit.type,
            videoHit.duration,
            videoHit.videos.medium.thumbnail,
            videoHit.videos.medium.url,
        )
    }
}
