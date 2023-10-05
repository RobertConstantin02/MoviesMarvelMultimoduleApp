package com.example.feature_feed.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
import com.example.presentation_mapper.BoToVoCharacterPresentationMapper.toCharacterPresentationScreenVO
import com.example.presentation_model.CharacterPresentationScreenVO
import com.example.resources.RemoteError
import com.example.usecase.character.IGetCharacterDetailsUseCase
import com.example.usecase.character.Params
import com.example.usecase.di.GetCharacterDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    @GetCharacterDetails private val getCharacterDetails: IGetCharacterDetailsUseCase
) : ViewModel() {

    private val _characterDetailState =
        MutableStateFlow<CharacterDetailPSState>(CharacterDetailPSState.Loading)
    val characterDetailState: MutableStateFlow<CharacterDetailPSState> = _characterDetailState

    fun getCharacterDetails() {
        getCharacterDetails.invoke(
            Params(2, 20),
            Dispatchers.IO,
            viewModelScope,
            error = {
                Log.d("-----> Error", it.message.toString())
            },
            success = this::onSuccess
        )
    }

    fun onEvent(event: CharacterDetailPSEvent) {
        when (event) {
            is CharacterDetailPSEvent.Found -> {
                _characterDetailState.update {
                    CharacterDetailPSState.Success(event.characterPresentationScreen)
                }
            }

            is CharacterDetailPSEvent.NotFound -> {

            }

            is CharacterDetailPSEvent.ServerError -> {

            }
        }
    }

    private fun onSuccess(characterPS: CharacterPresentationScreenBO) =
        onEvent(
            CharacterDetailPSEvent.Found(characterPS.toCharacterPresentationScreenVO())
        )

    private fun onError(remoteError: RemoteError) {

    }
}

//data class CharacterPresentationScreenVO(
//    val characterMainDetail: CharacterDetailBo?,
//    val extendedLocation: ExtendedLocationBo?,
//    val neighbors: List<CharacterNeighborBo>?,
//    val episodes: List<EpisodeBo>?
//)
