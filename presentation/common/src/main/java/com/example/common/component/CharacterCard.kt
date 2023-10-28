package com.example.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.common.R
import com.example.designsystem.icon.AppIcons
import com.example.designsystem.theme.LocalSpacing
import com.example.presentation_model.CharacterVo

@Composable
fun CharacterCard(
    item: () -> CharacterVo,
    onItemClick: (itemId: Int, locationId: Int?)-> Unit,
    onToggleSave: (isFavorite: Boolean, characterId: Int) -> Unit,
    modifier: Modifier
) {
    val dimens = LocalSpacing.current

    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(dimens.spaceSmall)
            .shadow(
                elevation = dimens.spaceMedium,
                shape = RoundedCornerShape(dimens.spaceMedium),
                spotColor = MaterialTheme.colorScheme.onSurface
            )
            .clickable { onItemClick(item().id, item().locationId) },
        shape = RoundedCornerShape(dimens.spaceSmall),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        ) {
            Image(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = dimens.spaceExtraSmall,
                            topEnd = dimens.spaceExtraSmall
                        )
                    ),
                painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current)
                    .data(data = item().image)
                    .placeholder(R.drawable.ic_placeholder).crossfade(true)
                    .apply(block = fun ImageRequest.Builder.() {
                        size(Size.ORIGINAL)
                    }).error(R.drawable.ic_placeholder).build()
                ),
                contentDescription = item().name,
                contentScale = ContentScale.FillWidth,
            )
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(dimens.spaceExtraSmall)
            ) {
                Row(modifier = modifier.fillMaxWidth()) {
                    Text(
                        modifier = modifier.weight(0.5f),
                        text = item().name.orEmpty(),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    SaveCharacterButton(
                        isSaved = item().isFavorite,
                        onClick = { onToggleSave(it, item().id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SaveCharacterButton(
    isSaved: Boolean,
    onClick: (Boolean) -> Unit
) {
    ToggleButton(
        checked = isSaved,
        onCheckedChange = onClick,
        uncheckedIcon = {
            Icon(
                imageVector = AppIcons.saveToggleButtonBorder,
                contentDescription = stringResource(R.string.unsaved),
            )
        },
        checkedIcon = {
            Icon(
                imageVector = AppIcons.saveToggleButtonFilled,
                contentDescription = stringResource(R.string.save),
            )
        }
    )
}