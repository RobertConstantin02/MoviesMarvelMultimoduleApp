package com.example.remote.character.datasource

import com.example.api.network.RickAndMortyService
import com.example.remote.extension.toRickAndMortyService
import com.example.remote.fake.ApiErrorHandlerFake
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

class CharacterRemoteDataSourceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiErrorHandler: ApiErrorHandlerFake
    private lateinit var rickAndMortyService : RickAndMortyService
    private lateinit var characterRemoteDataSource: CharacterRemoteDataSource
    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        apiErrorHandler = ApiErrorHandlerFake()
        rickAndMortyService = mockWebServer.toRickAndMortyService(apiErrorHandler)
        characterRemoteDataSource = CharacterRemoteDataSource(rickAndMortyService)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}