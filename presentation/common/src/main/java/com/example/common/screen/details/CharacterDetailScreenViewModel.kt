package com.example.common.screen.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.screen.ScreenState
import com.example.common.screen.ScreenStateEvent
import com.example.common.util.translateError
import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
import com.example.domain_model.error.DomainUnifiedError
import com.example.presentation_mapper.BoToVoCharacterPresentationMapper.toCharacterPresentationScreenVO
import com.example.presentation_model.CharacterPresentationScreenVO
import com.example.usecase.character.IGetCharacterDetailsUseCase
import com.example.usecase.di.GetCharacterDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    @GetCharacterDetails private val getCharacterDetails: IGetCharacterDetailsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _characterDetailState =
        MutableStateFlow<ScreenState<CharacterPresentationScreenVO>>(ScreenState.Loading())
    val characterDetailState: MutableStateFlow<ScreenState<CharacterPresentationScreenVO>> = _characterDetailState

    fun onEvent(event: CharacterDetailScreenEvent<CharacterPresentationScreenVO>) {
        when (event) {
            is CharacterDetailScreenEvent.OnGetCharacterDetails -> getCharacterDetails()

            is CharacterDetailScreenEvent.OnScreenState -> {
                when(event.screenStateEvent) {
                    is ScreenStateEvent.OnError -> {
                        _characterDetailState.update {
                            ScreenState.Error(event.screenStateEvent.error, event.screenStateEvent.data)
                        }
                    }
                    is ScreenStateEvent.OnSuccess -> {
                        _characterDetailState.update {
                            ScreenState.Success(event.screenStateEvent.data)
                        }
                    }
                }
            }
        }
    }

    private fun getCharacterDetails() {
        // TODO: some character from list have null locartionId or characterId
        getCharacterDetails.invoke(
            IGetCharacterDetailsUseCase.Params(
                savedStateHandle.get<String>(CHARACTER_ID)?.toInt() ?: 0,
                savedStateHandle.get<String>(LOCATION_ID)?.toInt() ?: 0
            ),
            viewModelScope,
            error = ::onError,
            success = ::onSuccess,
            empty = { Log.d("-----> empty", "called") }
        )
    }

    private fun onSuccess(characterPS: CharacterPresentationScreenBO) =
        onEvent(CharacterDetailScreenEvent.OnScreenState(ScreenStateEvent.OnSuccess(characterPS.toCharacterPresentationScreenVO())))

    private fun onError(error: DomainUnifiedError, data: CharacterPresentationScreenBO?) {
        onEvent(CharacterDetailScreenEvent.OnScreenState(translateError(error, data?.toCharacterPresentationScreenVO())))
    }

    companion object {
        private const val CHARACTER_ID = "characterId"
        private const val LOCATION_ID = "locationId"
    }
}
