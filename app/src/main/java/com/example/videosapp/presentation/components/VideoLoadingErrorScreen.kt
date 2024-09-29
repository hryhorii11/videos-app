package com.example.videosapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.videosapp.R
import com.example.videosapp.data.model.VideoError
import com.example.videosapp.data.model.VideoHit
import com.example.videosapp.presentation.utils.UIState

@Composable
fun VideoLoadingErrorScreen(videosState: UIState.Error<List<VideoHit>>, reload: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.average_padding)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = stringResource(R.string.error_icon),
            modifier = Modifier.size(dimensionResource(id = R.dimen.error_icon_size)),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.average_padding)))

        Text(
            text = when (videosState.error) {
                is VideoError.NetworkError -> stringResource(R.string.internet_exception_message)
                is VideoError.ServerError -> stringResource(R.string.server_error_message)
                is VideoError.ApiError -> stringResource(R.string.api_error) + videosState.error.message
                is VideoError.UnknownError -> stringResource(R.string.unknown_error_message)
                else -> stringResource(R.string.other_error_message)
            },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.big_spacer_height)))

        Button(
            onClick = { reload() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(stringResource(R.string.try_again_button_text))
        }
    }
}