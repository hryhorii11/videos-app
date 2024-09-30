package com.example.videosapp.presentation.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videosapp.data.model.VideoHit
import com.example.videosapp.data.repository.VideoRepository
import com.example.videosapp.presentation.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val repository: VideoRepository
) : ViewModel() {

    private val _videos = MutableStateFlow<UIState<List<VideoHit>>>(UIState.Idle())
    val videos: StateFlow<UIState<List<VideoHit>>> = _videos

    init {
        setVideos()
    }
    fun setVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            _videos.value = UIState.Loading()
            repository.getVideos().collect {
                if (it.isSuccess) _videos.value = UIState.Success(it.getOrNull()!!)
                else _videos.value = UIState.Error(it.exceptionOrNull())
            }
        }
    }
}