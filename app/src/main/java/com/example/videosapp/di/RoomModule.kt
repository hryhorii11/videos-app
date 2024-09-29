package com.example.videosapp.di

import android.app.Application
import com.example.videosapp.data.room.VideosDao
import com.example.videosapp.data.room.VideosDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Provides
    @Singleton
    fun provideDataBase(app: Application): VideosDataBase {
        return VideosDataBase.getInstance(app)
    }

    @Provides
    @Singleton
    fun provideResultsDao(db: VideosDataBase): VideosDao = db.getVideoDao()

}