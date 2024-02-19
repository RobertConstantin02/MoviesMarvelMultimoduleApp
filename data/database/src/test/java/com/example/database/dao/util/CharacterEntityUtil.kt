package com.example.database.dao.util

import com.example.database.entities.CharacterEntity
import com.example.test.FileUtil
import com.example.test.GsonAdapterExt.fromJson

const val ALL_CHARACTERS_ENTITY_JSON = "json/getAllCharactersEntity.json"

object CharacterEntityUtil {
    val expectedCharactersEntity = FileUtil.getJson(ALL_CHARACTERS_ENTITY_JSON)?.fromJson<List<CharacterEntity>>() as List<CharacterEntity>
}