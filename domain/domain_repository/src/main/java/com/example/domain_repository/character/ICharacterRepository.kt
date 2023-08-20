package com.example.domain_repository.character

import androidx.paging.PagingData
import com.example.domain_model.CharacterFeedBo
import kotlinx.coroutines.flow.Flow


interface ICharacterRepository {
    fun getAllCharacters(): Flow<PagingData<CharacterFeedBo>>
}