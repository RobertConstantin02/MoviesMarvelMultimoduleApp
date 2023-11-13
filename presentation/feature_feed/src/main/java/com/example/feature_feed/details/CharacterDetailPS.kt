package com.example.feature_feed.details

import android.annotation.SuppressLint
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.R
import com.example.common.component.CircularLoadingBar
import com.example.common.component.ErrorScreen
import com.example.common.component.ExpandButton
import com.example.common.component.ExpandableContent
import com.example.common.component.ImageFromUrlFullWidth
import com.example.designsystem.icon.AppIcons
import com.example.designsystem.theme.LocalSpacing
import com.example.presentation_model.CharacterNeighborVO
import com.example.presentation_model.CharacterPresentationScreenVO
import com.example.presentation_model.EpisodeVO

@Composable
fun DetailPresentationScreen(
    viewModel: DetailViewModel = hiltViewModel()
) {
    val characterDetailState by viewModel.characterDetailState.collectAsStateWithLifecycle()
    viewModel.onEvent(CharacterDetailPSEvent.OnGetCharacterDetails)
    DetailPresentationScreenContent(characterDetail = { characterDetailState })
}

@Composable
fun DetailPresentationScreenContent(
    characterDetail: () -> CharacterDetailPSState
) {
    val context = LocalContext.current
    when (val state = characterDetail()) {
        is CharacterDetailPSState.Loading ->
            CircularLoadingBar(stringResource(id = R.string.character_detail_loading))

        is CharacterDetailPSState.Success ->
            DetailSuccess(characterDetail = { state.characterDetail })

        is CharacterDetailPSState.Error ->
            ErrorScreen(state.characterDetailError.message.asString(context)) {
                // TODO: retry and ask data again
            }
    }
}

@Composable
fun DetailSuccess(
    characterDetail: () -> CharacterPresentationScreenVO
) {
    val dimens = LocalSpacing.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimens.spaceSmall),
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
                DetailBody(characterDetail = { characterDetail() })
            }
            item {
                EpisodesDetailFooter(episodes = { characterDetail().episodes })
            }
            item {
                NeighborDetailFooter(neighbors = { characterDetail().neighbors })
            }
        }
    }
}

@Composable
fun DetailHeader(
    characterDetail: () -> CharacterPresentationScreenVO
) {
    val dimens = LocalSpacing.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
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
            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
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

    //Spacer(modifier = Modifier.height(dimens.spaceMedium))

    DetailBodyContent(
        characterDetail = characterDetail,
        isExpanded = { expanded },
        onExpandShrinkClick = { expanded = it },
        cardRoundedCorners = { cardRoundedCorners },
        cardBgColor = { cardBgColor }
    )
}

@Composable
fun DetailBodyContent(
    characterDetail: () -> CharacterPresentationScreenVO,
    isExpanded: () -> Boolean,
    onExpandShrinkClick: (Boolean) -> Unit,
    cardRoundedCorners: () -> Dp,
    cardBgColor: () -> Color,
) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = characterDetail().characterMainDetail?.name.orEmpty(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Left
        )
        LocationCard(
            characterDetail = { characterDetail() },
            isExpanded = { isExpanded() },
            onExpandShrinkClick = onExpandShrinkClick,
            cardRoundedCorners,
            cardBgColor
        )
    }
}

@Composable
fun LocationCard(
    characterDetail: () -> CharacterPresentationScreenVO,
    isExpanded: () -> Boolean,
    onExpandShrinkClick: (Boolean) -> Unit,
    cardRoundedCorners: () -> Dp,
    cardBgColor: () -> Color,
) {
    val dimens = LocalSpacing.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(cardRoundedCorners()),
        colors = CardDefaults.cardColors(
            containerColor = cardBgColor()
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.spaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = AppIcons.extraInfoIcon,
                contentDescription = stringResource(id = R.string.icon_location_content_description),
            )
            ExpandButton(expanded = { isExpanded() }, action = { onExpandShrinkClick(it) })
        }
        if (isExpanded()) Divider()
        ExpandableContent(visible = isExpanded()) {
            CharacterDescriptionContent(characterDetail())
        }
    }

}

@Composable
private fun CharacterDescriptionContent(
    character: CharacterPresentationScreenVO,
    modifier: Modifier = Modifier
) {
    val dimens = LocalSpacing.current
    Column(
        modifier = modifier.padding(dimens.spaceMedium),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        with(character) {
            DetailRowContent(R.string.character_specimen) { characterMainDetail?.specimen }
            DetailRowContent(R.string.character_origin) { characterMainDetail?.originName }
            DetailRowContent(R.string.location_name) { extendedLocation?.name }
            DetailRowContent(R.string.location_type) { extendedLocation?.type }
            DetailRowContent(R.string.location_dimension) { extendedLocation?.dimension }
        }
    }
}

@Composable
private fun DetailRowContent(nameResource: Int, value: () -> String?) {
    val dimens = LocalSpacing.current
    Row(
        modifier = Modifier.padding(
            start = dimens.spaceMedium,
            end = dimens.spaceSmall,
            bottom = dimens.spaceMedium,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = nameResource),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.width(dimens.spaceSmall))
        Text(
            text = value()?.ifEmpty { stringResource(id = R.string.detail_unknown) }
                ?: stringResource(id = R.string.detail_unknown),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
internal fun EpisodesDetailFooter(episodes: () -> List<EpisodeVO>?) {
    val dimens = LocalSpacing.current
    val state = rememberLazyListState()
    var visible by remember { mutableStateOf(episodes()?.isNotEmpty() == true) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimens.spaceMedium),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        TextAnimation(isVisible = visible) {
            Text(
                text = stringResource(id = R.string.episodes_row_title),
                style = MaterialTheme.typography.titleLarge,
            )
        }
        Spacer(modifier = Modifier.height(dimens.spaceMedium))
        ListAnimation(isVisible = episodes()?.isNotEmpty() == true) {
            episodes()?.let { episodes ->
                LazyRow(
                    state = state,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    content = {
                        itemsIndexed(items = episodes, key = { _, item ->
                            item.id
                        }) { _, item ->
                            EpisodeItemContent(item)
                        }
                    })
            }
        }
    }
}

@Composable
internal fun NeighborDetailFooter(
    neighbors: () -> List<CharacterNeighborVO>?
) {
    val dimens = LocalSpacing.current
    val state = rememberLazyListState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimens.spaceMedium, bottom = dimens.spaceMedium),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        TextAnimation(isVisible = neighbors()?.isNotEmpty() == true) {
            Text(
                text = stringResource(id = R.string.neighbor_row_title),
                style = MaterialTheme.typography.titleSmall,
            )
        }
        Spacer(modifier = Modifier.height(dimens.spaceMedium))
        ListAnimation(isVisible = neighbors()?.isNotEmpty() == true) {
            neighbors()?.let { neighbors ->
                LazyRow(
                    state = state,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    content = {
                        itemsIndexed(items = neighbors, key = { _, item ->
                            item.id
                        }) { _, item ->
                            NeighborItemContent(item)
                        }
                    })
            }
        }
    }
}

@Composable
internal fun EpisodeItemContent(episode: EpisodeVO) {
    val dimens = LocalSpacing.current
    val unknownField = stringResource(id = R.string.detail_unknown)
    Card(
        Modifier
            .padding(end = dimens.spaceSmall)
            .shadow(
                elevation = dimens.spaceSmall,
                shape = RoundedCornerShape(dimens.spaceSmall)
            ),
        shape = RoundedCornerShape(topEnd = dimens.spaceSmall),
    ) {
        Text(
            text = episode.name ?: unknownField,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.spaceSmall)
        )
        Text(
            text = episode.episode ?: unknownField,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.spaceSmall)
        )
        Text(
            text = episode.date ?: unknownField,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.spaceSmall)
        )
    }
}

@Composable
internal fun NeighborItemContent(neighborVO: CharacterNeighborVO) {
    val dimens = LocalSpacing.current
    Card(
        Modifier
            .padding(end = dimens.spaceSmall)
            .shadow(
                elevation = dimens.spaceSmall,
                shape = RoundedCornerShape(dimens.spaceSmall)
            ),
        shape = RoundedCornerShape(topEnd = dimens.spaceSmall),
    ) {
        ImageFromUrlFullWidth(url = { neighborVO.image })
    }
}

@Composable
fun TextAnimation(isVisible: Boolean, text: @Composable () -> Unit) {
    val density = LocalDensity.current
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { with(density) { 10.dp.roundToPx() } },
        ) + expandHorizontally(
            expandFrom = Alignment.End,
        ) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutHorizontally() + shrinkHorizontally() + fadeOut()
    ) {
        text()
    }
}

@Composable
fun ListAnimation(isVisible: Boolean, list: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(animationSpec = tween(
            300,
            easing = { OvershootInterpolator().getInterpolation(it) }),
            initialOffsetX = { 400 }) + fadeIn(),
        exit = fadeOut()
    ) {
        list()
    }
}


