package com.example.common.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.example.common.R
import com.example.designsystem.theme.LocalSpacing

@Composable
fun ImageFromUrlFullWidth(
    url: () -> String,
    modifier: Modifier = Modifier
) {
    val dimens = LocalSpacing.current
    ImageFromUrl(
        url = url,
        modifier
            .fillMaxWidth()
            .shadow(
                elevation = dimens.spaceSmall,
                shape = RoundedCornerShape(dimens.spaceSmall),
                spotColor = MaterialTheme.colorScheme.primary
            )
    )
}

@Composable
fun ImageFromUrlDraw(
    url: () -> String,
    modifier: Modifier = Modifier,
    draw: CacheDrawScope.() -> DrawResult = { onDrawWithContent {} }
) {
    val dimens = LocalSpacing.current
    ImageFromUrl(
        url = url,
        modifier
            .fillMaxWidth()
            .shadow(
                elevation = dimens.spaceSmall,
                shape = RoundedCornerShape(dimens.spaceSmall),
                spotColor = MaterialTheme.colorScheme.primary
            ).drawWithCache { draw() }
    )
}

@Composable
private fun ImageFromUrl(
    url: () -> String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = ImageRequest
            .Builder(LocalContext.current)
            .data(url())
            .placeholder(R.drawable.ic_placeholder)
            .size(Size.ORIGINAL)
            .crossfade(true).build(),
        contentDescription = stringResource(id = R.string.character_detail_image),
        modifier = modifier,
        contentScale = ContentScale.FillWidth,
        error = painterResource(id = R.drawable.ic_placeholder),
    )
}