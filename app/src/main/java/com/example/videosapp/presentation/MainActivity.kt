package com.example.videosapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.videosapp.presentation.navigation.Navigation
import com.example.videosapp.presentation.ui.theme.VideosAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideosAppTheme {
                Navigation()
            }
        }
    }
}

