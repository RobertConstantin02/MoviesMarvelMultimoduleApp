package com.example.common.screen.details

import com.example.domain_model.character.CharacterNeighborBo
import com.example.domain_model.characterDetail.CharacterDetailBo
import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
import com.example.domain_model.common.ImageUrlBo
import com.example.domain_model.episode.EpisodeBo
import com.example.domain_model.location.ExtendedLocationBo
import com.example.domain_model.location.LocationBo

fun characterPresentationScreen() = CharacterPresentationScreenBO(
    characterMainDetail = CharacterDetailBo(
        1,
        "Rick Sanchez",
        "Alive",
        "Human",
        LocationBo(
            "https://rickandmortyapi.com/api/location/3",
            "Citadel of Ricks"
        ),
        "Earth (C-137)",
        "Male",
        ImageUrlBo("https://rickandmortyapi.com/api/character/avatar/1.jpeg"),
        listOf(
            "https://rickandmortyapi.com/api/episode/1",
            "https://rickandmortyapi.com/api/episode/2",
            "https://rickandmortyapi.com/api/episode/3",
            "https://rickandmortyapi.com/api/episode/4"
        )
    ),
    ExtendedLocationBo(
        1,
        "Citadel of Ricks",
        "Space station",
        "Testicle Monster Dimension",
        listOf(
            "https://rickandmortyapi.com/api/character/1",
            "https://rickandmortyapi.com/api/character/2",
            "https://rickandmortyapi.com/api/character/3",
            "https://rickandmortyapi.com/api/character/4"
        )
    ),
    listOf(
        CharacterNeighborBo(
            1,
            ImageUrlBo("https://rickandmortyapi.com/api/character/avatar/1.jpeg")
        ),
        CharacterNeighborBo(
            2,
            ImageUrlBo("https://rickandmortyapi.com/api/character/avatar/2.jpeg")
        )
    ),
    episodes = listOf(
        EpisodeBo(
            1,
            "Close Rick-counters of the Rick Kind",
            "S01E10",
            "April 7, 2014"
        ),
        EpisodeBo(
            2,
            "The Ricklantis Mixup",
            "S03E07",
            "September 10, 2017"
        )
    )
)