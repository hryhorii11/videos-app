package com.example.videosapp.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VideosDao {

    @Insert
    fun addVideos(videoEntities: List<VideoEntity>)

    @Query("select * from videos")
    fun getAllVideos(): List<VideoEntity>

    @Query("DELETE FROM videos")
    fun clearVideos()

}