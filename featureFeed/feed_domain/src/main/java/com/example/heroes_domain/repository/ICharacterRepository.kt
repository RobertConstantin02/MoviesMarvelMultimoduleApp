package com.example.heroes_domain.repository

import androidx.paging.PagingData
import com.example.heroes_domain.model.CharacterFeedBo
import com.example.resources.Result
import kotlinx.coroutines.flow.Flow

interface ICharacterRepository {
    fun getAllCharacters(): Flow<PagingData<CharacterFeedBo>>
}