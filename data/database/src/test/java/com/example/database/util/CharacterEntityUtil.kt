package com.example.database.util

import com.example.database.entities.CharacterEntity
import com.example.database.entities.LocationEntity
import com.example.database.entities.OriginEntity
import com.example.test.FileUtil
import com.example.test.GsonAdapterExt.fromJson

const val ALL_CHARACTERS_ENTITY_JSON = "json/getAllCharactersEntity.json"

object CharacterEntityUtil {
    val expectedCharactersEntity = FileUtil.getJson(ALL_CHARACTERS_ENTITY_JSON)
        ?.fromJson<List<CharacterEntity>>() as List<CharacterEntity>

    fun createCharacters(number: Int) = List(number) {
        val randomIsFav = (0..1).random()
        CharacterEntity(
            id = it + 1,
            name = "Character ${it +1}",
            "asas",
            "null",
            LocationEntity("as", "as"),
            OriginEntity("as"),
            "null",
            "null",
            listOf(
                "https://rickandmortyapi.com/api/episode/1",
                "https://rickandmortyapi.com/api/episode/2",
                "https://rickandmortyapi.com/api/episode/3",
                "https://rickandmortyapi.com/api/episode/4",
                "https://rickandmortyapi.com/api/episode/5"
            ),
            isFavorite = randomIsFav != 0
        )
    }

    fun createCharacter(id: Int) =
        CharacterEntity(
            id = id,
            name = "Character $id",
            "fake",
            "fake",
            LocationEntity("fake", "fake"),
            OriginEntity("fake"),
            "fake",
            "fake",
            listOf(
                "https://rickandmortyapi.com/api/episode/1",
                "https://rickandmortyapi.com/api/episode/2",
                "https://rickandmortyapi.com/api/episode/3",
                "https://rickandmortyapi.com/api/episode/4",
                "https://rickandmortyapi.com/api/episode/5"
            )
        )
}