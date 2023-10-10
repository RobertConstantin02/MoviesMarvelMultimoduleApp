package com.example.feature_feed.details

import android.annotation.SuppressLint
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.R
import com.example.common.component.CircularLoadingBar
import com.example.common.component.ErrorScreen
import com.example.common.component.ImageFromUrlFullWidth
import com.example.designsystem.theme.LocalSpacing
import com.example.presentation_model.CharacterPresentationScreenVO

@Composable
fun DetailPresentationScreen(
    viewModel: DetailViewModel = hiltViewModel()
) {
    val characterdetailState by viewModel.characterDetailState.collectAsStateWithLifecycle()
    viewModel.getCharacterDetails()
    DetailPresentationScreenContent(characterDetail = { characterdetailState })
}

@Composable
fun DetailPresentationScreenContent(
    characterDetail: () -> CharacterDetailPSState
) {
    when (val state = characterDetail()) {
        is CharacterDetailPSState.Loading ->
            CircularLoadingBar(stringResource(id = R.string.character_detail_loading))

        is CharacterDetailPSState.Success -> {
            DetailSuccess(characterDetail = { state.characterDetail })
        }

        is CharacterDetailPSState.Error -> ErrorScreen(R.string.detail_try_again) {
            // TODO: try again with refresh()
        }
    }
}

@Composable
fun DetailSuccess(
    characterDetail: () -> CharacterPresentationScreenVO
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = rememberLazyListState()
        ) {
            item {
                DetailHeader(characterDetail = { characterDetail() })
            }
            item {

            }
        }
    }
}

@Composable
fun DetailHeader(
    characterDetail: () -> CharacterPresentationScreenVO
) {
    val dimens = LocalSpacing.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimens.spaceSmall)
            .shadow(
                elevation = dimens.spaceExtraSmall,
                shape = RoundedCornerShape(dimens.spaceSmall),
                spotColor = MaterialTheme.colorScheme.primary
            )
    ) {
        ImageFromUrlFullWidth(
            url = { characterDetail().characterMainDetail?.image.orEmpty() },
        )
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun DetailBody(
    characterDetail: () -> CharacterPresentationScreenVO
) {
    val dimens = LocalSpacing.current
    var expanded by remember { mutableStateOf(false) }
    val transitionState = remember {
        MutableTransitionState(expanded).apply {
            targetState = !expanded
        }
    }
    val transition = updateTransition(
        targetState = transitionState,
        label = stringResource(R.string.transitionLabel)
    )

    val cardBgColor by transition.animateColor({
        tween(durationMillis = 200)
    }, label = stringResource(R.string.background_transition_label)) {
        if (expanded) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        } else {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        }
    }
    val cardRoundedCorners by transition.animateDp(
        {
            tween(durationMillis = 200, easing = FastOutSlowInEasing)
        },
        label = stringResource(R.string.rounded_corners_label),
    ) {
        if (expanded) dimens.spaceSmall else dimens.spaceMedium
    }


}

