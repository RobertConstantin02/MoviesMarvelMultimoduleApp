package com.example.remote.util

import com.example.api.model.character.CharacterDto
import com.example.remote.extension.GsonAdapterExt
import com.example.remote.extension.GsonAdapterExt.fromJson

const val EMPTY_JSON = "json/empty.json"
object CharacterUtil {
    val expectedEmptyCharacterResponse = FileUtil.getJson(EMPTY_JSON)?.fromJson<CharacterDto>() as CharacterDto
}