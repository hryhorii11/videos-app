package com.example.videosapp.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [
        VideoEntity::class
    ]
)
abstract class VideosDataBase : RoomDatabase() {
    abstract fun getVideoDao(): VideosDao

    companion object {
        private const val DATABASE_NAME = "video_db"

        @Volatile
        private var INSTANCE: VideosDataBase? = null
        fun getInstance(context: Context): VideosDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VideosDataBase::class.java,
                    DATABASE_NAME
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }


}