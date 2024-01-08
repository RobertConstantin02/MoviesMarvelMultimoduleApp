package com.example.common.screen.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
import com.example.presentation_mapper.BoToVoCharacterPresentationMapper.toCharacterPresentationScreenVO
import com.example.usecase.character.IGetCharacterDetailsUseCase
import com.example.usecase.di.GetCharacterDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    @GetCharacterDetails private val getCharacterDetails: IGetCharacterDetailsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _characterDetailState =
        MutableStateFlow<CharacterDetailPSState>(CharacterDetailPSState.Loading)
    val characterDetailState: MutableStateFlow<CharacterDetailPSState> = _characterDetailState

    fun onEvent(event: CharacterDetailPSEvent) {
        when (event) {
            is CharacterDetailPSEvent.OnGetCharacterDetails -> getCharacterDetails()

            is CharacterDetailPSEvent.Found ->
                _characterDetailState.update {
                    CharacterDetailPSState.Success(event.characterPresentationScreen)
                }

            is CharacterDetailPSEvent.Error -> _characterDetailState.update { event.error }
        }
    }

    private fun getCharacterDetails() {
        // TODO: some character from list have null locartionId or characterId
        getCharacterDetails.invoke(
            IGetCharacterDetailsUseCase.Params(
                savedStateHandle.get<String>(CHARACTER_ID)?.toInt() ?: 0,
                savedStateHandle.get<String>(LOCATION_ID)?.toInt() ?: 0
            ),
            Dispatchers.IO,
            viewModelScope,
            error = ::onError,
            success = ::onSuccess,
            empty = { Log.d("-----> empty", "called") }
        )
    }

    private fun onSuccess(characterPS: CharacterPresentationScreenBO) =
        onEvent(
            CharacterDetailPSEvent.Found(characterPS.toCharacterPresentationScreenVO())
        )

    private fun onError(apiError: String?, localError: Int?, data: CharacterPresentationScreenBO?) {
        data?.let {
            onEvent(
                CharacterDetailPSEvent.Found(data.toCharacterPresentationScreenVO())
            )
        } ?: onEvent(CharacterDetailPSEvent.Error(CharacterDetailPSState.Error(apiError, localError)))
    }


    companion object {
        private const val CHARACTER_ID = "characterId"
        private const val LOCATION_ID = "locationId"
    }
}
