package com.example.usecase.character.fake

import androidx.paging.PagingData
import com.example.domain_model.character.CharacterBo
import com.example.domain_model.character.CharacterNeighborBo
import com.example.domain_model.characterDetail.CharacterDetailBo
import com.example.domain_model.error.DomainApiUnifiedError
import com.example.domain_model.error.DomainUnifiedError
import com.example.domain_model.resource.DomainResource
import com.example.domain_repository.character.ICharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

const val TEST_ERROR_MESSAGE  = "Error Test"
class CharacterRepositoryFake: ICharacterRepository {

    private val charactersBo = MutableStateFlow<List<CharacterBo>>(emptyList())
    private val charactersDetailBo = MutableStateFlow<List<CharacterDetailBo>>(emptyList())
    private val charactersNeighborBo = MutableStateFlow<List<CharacterNeighborBo>>(emptyList())

    var apiError: DomainUnifiedError? = null
    var localError: DomainUnifiedError? = null

    override fun getAllCharacters(): Flow<PagingData<CharacterBo>> =
        flowOf(PagingData.from(charactersBo.value))

    override fun getCharacter(characterId: Int): Flow<DomainResource<CharacterDetailBo>> {
        if (apiError != null && localError != null) flowOf(DomainResource.error(apiError!!, null))
    }

    override fun getCharactersByIds(charactersIds: List<Int>): Flow<DomainResource<List<CharacterNeighborBo>>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCharacterIsFavorite(
        isFavorite: Boolean,
        characterId: Int
    ): Flow<DomainResource<Unit>> {
        TODO("Not yet implemented")
    }

    override fun getFavoriteCharacters(
        page: Int,
        offset: Int
    ): Flow<DomainResource<List<CharacterBo>>> {
        TODO("Not yet implemented")
    }
}