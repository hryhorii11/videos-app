package com.example.videosapp.presentation.utils


sealed class UIState<T> {
    class Idle<T> : UIState<T>()
    class Loading<T> : UIState<T>()
    class Error<T>(val error: Throwable?) : UIState<T>()
    class Success<T>(val data: T) : UIState<T>()
}