package com.example.feature_feed.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.R
import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
import com.example.presentation_mapper.BoToVoCharacterPresentationMapper.toCharacterPresentationScreenVO
import com.example.resources.DataBaseError
import com.example.resources.DataSourceError
import com.example.resources.RemoteError
import com.example.resources.UiText
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
            success = ::onSuccess
        )
    }

    private fun onSuccess(characterPS: CharacterPresentationScreenBO) =
        onEvent(
            CharacterDetailPSEvent.Found(characterPS.toCharacterPresentationScreenVO())
        )

    private fun onError(error: DataSourceError) {
        val errorEvent = when (error) {
            is RemoteError -> checkRemoteError(error)
            is DataBaseError -> checkLocalDbError(error)
        }
        onEvent(CharacterDetailPSEvent.Error(errorEvent))
    }

    private fun checkRemoteError(error: RemoteError) =
        when (error) {
            is RemoteError.Server -> CharacterDetailPSState.Error(
                CharacterDetailError.ServerError(
                    UiText.DynamicText(R.string.remote_server_error, error.codeError.toString())
                )
            )

            is RemoteError.Connectivity -> CharacterDetailPSState.Error(
                CharacterDetailError.ConnectivityError(
                    UiText.StringResources(R.string.remote_connectivity_error)
                )
            )

            is RemoteError.Unknown -> CharacterDetailPSState.Error(
                CharacterDetailError.UnknownError(
                    UiText.StringResources(R.string.remote_unknown_error)
                )
            )
        }

    private fun checkLocalDbError(error: DataBaseError) =
        when (error) {
            is DataBaseError.InsertionError -> getLocalDbErrorMessage(R.string.local_db_insertion_error)
            is DataBaseError.DeletionError -> getLocalDbErrorMessage(R.string.local_db_deletion_error)
            is DataBaseError.EmptyResult -> getLocalDbErrorMessage(R.string.local_db_empty_result)
            is DataBaseError.ItemNotFound -> getLocalDbErrorMessage(R.string.local_db_item_not_found)
            is DataBaseError.UpdateError -> getLocalDbErrorMessage(R.string.local_db_item_not_found) //refactor
        }

    private fun getLocalDbErrorMessage(resourcesMessage: Int) = CharacterDetailPSState.Error(
        CharacterDetailError.DataBasError(
            UiText.StringResources(resourcesMessage)
        )
    )

    companion object {
        private const val CHARACTER_ID = "characterId"
        private const val LOCATION_ID = "locationId"
    }
}

//data class CharacterPresentationScreenVO(
//    val characterMainDetail: CharacterDetailBo?,
//    val extendedLocation: ExtendedLocationBo?,
//    val neighbors: List<CharacterNeighborBo>?,
//    val episodes: List<EpisodeBo>?
//)
