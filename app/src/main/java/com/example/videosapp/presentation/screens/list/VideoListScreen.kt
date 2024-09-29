package com.example.videosapp.presentation.screens.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.videosapp.R
import com.example.videosapp.data.model.VideoFormats
import com.example.videosapp.data.model.VideoHit
import com.example.videosapp.data.model.VideoInfo
import com.example.videosapp.presentation.components.LoadingScreen
import com.example.videosapp.presentation.components.VideoLoadingErrorScreen
import com.example.videosapp.presentation.navigation.Screens
import com.example.videosapp.presentation.ui.theme.VideosAppTheme
import com.example.videosapp.presentation.utils.UIState

@Composable
fun VideoListScreen(
    navHostController: NavHostController,
    viewModel: VideoListViewModel = hiltViewModel()
) {

    val videoState by viewModel.videos.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.setVideos()
    }

    VideoListScreenContent(
        videosState = videoState,
        reload = { viewModel.setVideos() },
        navHostController = navHostController
    )
}


@Composable
fun VideoListScreenContent(
    videosState: UIState<List<VideoHit>>,
    reload: () -> Unit,
    navHostController: NavHostController? = null
) {

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = stringResource(R.string.video_list_screen_title),
            fontSize = dimensionResource(id = R.dimen.title_text_size).value.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.average_padding))
        )
        when (videosState) {
            is UIState.Error -> {
                VideoLoadingErrorScreen(videosState, reload)
            }

            is UIState.Idle -> {}
            is UIState.Loading -> {
                LoadingScreen()
            }

            is UIState.Success -> {
                ListScreen(videosState, navHostController)
            }
        }

    }
}

@Composable
fun ListScreen(
    videosState: UIState.Success<List<VideoHit>>,
    navHostController: NavHostController?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(videosState.data) { video ->
            VideoItem(video) {
                navHostController?.navigate(
                    "${Screens.PLAYER_SCREEN}/${
                        video.id
                    }"
                )
            }
        }
    }
}


@Composable
fun VideoItem(
    video: VideoHit,
    play: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.small_padding)),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.card_elevation))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.film_item_size))
                .padding(dimensionResource(id = R.dimen.small_padding))
                .clickable {
                    play()
                }
        ) {
            AsyncImage(
                model = video.videos.medium.thumbnail,
                contentDescription = "Video preview",
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.film_icon_size))
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.icon_video)
            )

            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.average_padding)))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = video.tags,
                    fontSize = dimensionResource(id = R.dimen.medium_text_size).value.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = video.type,
                    fontSize = dimensionResource(id = R.dimen.small_text_size).value.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = video.duration.toString(),
                    fontSize = dimensionResource(id = R.dimen.small_text_size).value.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun VideoListScreenPreview() {

    VideosAppTheme {

        VideoListScreenContent(
            videosState = UIState.Success(
                listOf(
                    VideoHit(
                        0L,
                        "film",
                        "fun",
                        13,
                        VideoFormats(
                            VideoInfo(
                                "",
                                ""
                            )
                        )
                    )
                )
            ),
            {}
        )
    }
}