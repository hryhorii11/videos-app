package com.example.videosapp.presentation.screens.player

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.example.videosapp.R
import com.example.videosapp.data.model.VideoFormats
import com.example.videosapp.data.model.VideoHit
import com.example.videosapp.data.model.VideoInfo
import com.example.videosapp.presentation.components.LoadingScreen
import com.example.videosapp.presentation.components.VideoLoadingErrorScreen
import com.example.videosapp.presentation.utils.UIState
import com.example.videosapp.presentation.utils.showMessage


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PlayerScreen(
    navHostController: NavHostController,
    selectedVideoId: Long,
    viewModel: PlayerViewModel = hiltViewModel()
) {

    val videosState by viewModel.videos.collectAsState()


    when (videosState) {
        is UIState.Success -> {
            val videos = (videosState as UIState.Success<List<VideoHit>>).data
            PlayerScreenContent(
                videos = videos,
                selectedVideoId,
                onNavigationBack = {
                    navHostController.navigateUp()
                }
            )
        }

        is UIState.Error -> {
            VideoLoadingErrorScreen(videosState as UIState.Error<List<VideoHit>>) { viewModel.setVideos() }
        }

        is UIState.Loading -> {
            LoadingScreen()
        }

        is UIState.Idle -> {}
    }

}


@androidx.annotation.OptIn(UnstableApi::class)
@SuppressLint("OpaqueUnitKey")
@ExperimentalAnimationApi
@Composable
fun PlayerScreenContent(
    videos: List<VideoHit>,
    selectedVideo: Long,
    onNavigationBack: () -> Unit
) {
    val videoPositionToHideControllers = 200

    val context = LocalContext.current
    val connectionErrorText = stringResource(id = R.string.internet_exception_message)
    val unknownErrorText = stringResource(id = R.string.unknown_error_message)

    val videoTitleVisibility = remember { mutableStateOf(true) }
    val isControlVisible = remember { mutableStateOf(false) }
    val videoTitle = remember { mutableStateOf(videos.find { it.id == selectedVideo }?.tags) }

    val mediaItems = createMediaItemsFromVideoHits(videos)

    val currentVideoPosition = rememberSaveable { mutableLongStateOf(0L) }
    val currentMediaItemIndex =
        rememberSaveable { mutableIntStateOf(videos.indexOf(videos.find { it.id == selectedVideo })) }
    val isPlayerPaused = rememberSaveable { mutableStateOf(false) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            this.setMediaItems(mediaItems)
            this.seekTo(currentMediaItemIndex.intValue, currentVideoPosition.longValue)
            this.prepare()
            playWhenReady = true
            this.addListener(object : Player.Listener {

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    if (error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
                        error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS
                    ) {
                        context.showMessage(connectionErrorText)
                    } else {
                        context.showMessage(unknownErrorText)
                    }
                }
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    isPlayerPaused.value = !isPlaying
                }

                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                    if (player.contentPosition >= videoPositionToHideControllers) videoTitleVisibility.value =
                        false
                }
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    videoTitleVisibility.value = true
                    videoTitle.value = mediaItem?.mediaMetadata?.displayTitle.toString()
                }
            })
        }
    }

    LocalLifecycleOwner.current.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_START -> {
                    if (exoPlayer.isPlaying.not() && !isPlayerPaused.value) {
                        exoPlayer.play()
                    }
                }
                Lifecycle.Event.ON_STOP -> {
                    exoPlayer.pause()
                }
                else -> {
                }
            }
        }
    })

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.background(Color.Black)) {
            DisposableEffect(
                AndroidView(
                    factory = {
                        PlayerView(context).apply {
                            player = exoPlayer
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                            setShowNextButton(true)
                            setShowPreviousButton(true)
                            setShowFastForwardButton(false)
                            setShowRewindButton(false)
                            this.setControllerVisibilityListener(PlayerView.ControllerVisibilityListener { visibility ->
                                isControlVisible.value = visibility == View.VISIBLE
                            })

                        }
                    })
            ) {
                onDispose {
                    currentVideoPosition.longValue = exoPlayer.currentPosition
                    currentMediaItemIndex.intValue = exoPlayer.currentMediaItemIndex
                    exoPlayer.release()
                }
            }

        }
        if (isControlVisible.value) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_button_description),
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(dimensionResource(id = R.dimen.back_icon_size))
                    .padding(dimensionResource(id = R.dimen.back_icon_padding))
                    .clickable {
                        onNavigationBack()
                    }
            )
        }
    }
}

fun createMediaItemsFromVideoHits(videos: List<VideoHit>): MutableList<MediaItem> {
    return ArrayList<MediaItem>().also {
        for (video in videos)
            it.add(
                MediaItem.Builder()
                    .setUri(video.videos.medium.url)
                    .setTag(videos)
                    .setMediaMetadata(MediaMetadata.Builder().setDisplayTitle(video.tags).build())
                    .build()
            )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun PlayerScreenContentPreview() {

    PlayerScreenContent(
        videos = listOf(
            VideoHit(0L, "film", "fun", 13, VideoFormats(VideoInfo("", "")))
        ),
        selectedVideo = 0,
    ) {

    }
}