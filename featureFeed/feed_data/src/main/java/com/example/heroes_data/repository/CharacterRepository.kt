package com.example.heroes_data.repository

import androidx.paging.PagingData
import com.example.heroes_data.api.datasource.ICharacterRemoteDataSource
import com.example.heroes_domain.model.CharacterFeedBo
import com.example.heroes_domain.repository.ICharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CharacterRepository @Inject constructor(
    private val remoteDataSource: ICharacterRemoteDataSource
): ICharacterRepository {

    override fun getAllCharacters(): Flow<PagingData<CharacterFeedBo>> {
        TODO("Not yet implemented")
    }
}