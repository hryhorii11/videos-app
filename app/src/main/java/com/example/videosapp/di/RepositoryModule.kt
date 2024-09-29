package com.example.videosapp.di

import com.example.videosapp.data.remote.VideoService
import com.example.videosapp.data.repository.VideoRepository
import com.example.videosapp.data.repository.VideoRepositoryImpl
import com.example.videosapp.data.room.VideosDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideRepository(catsFactsService: VideoService, videosDao: VideosDao): VideoRepository =
        VideoRepositoryImpl(catsFactsService, videosDao)
}