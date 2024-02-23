package com.example.usecase.character.fake

import com.example.domain_model.characterDetail.CharacterDetailBo
import com.example.domain_model.error.DomainUnifiedError
import com.example.domain_model.location.ExtendedLocationBo
import com.example.domain_model.resource.DomainResource
import com.example.domain_repository.location.ILocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class LocationRepositoryFake: ILocationRepository {
    private val extendedLocations = MutableStateFlow<List<ExtendedLocationBo>?>(emptyList())

    var error: DomainUnifiedError? = null
    var databaseEmpty: DomainResource<Nothing>? = null

    fun setExtendedLocation(characters: List<ExtendedLocationBo>?) {
        this.extendedLocations.value = characters
    }
    override fun getExtendedLocation(extendedLocationId: Int): Flow<DomainResource<ExtendedLocationBo>> {
        if (error != null) return flowOf(DomainResource.error(error!!, extendedLocations.value?.firstOrNull { location -> location.id == extendedLocationId }))
        if (databaseEmpty != null) return flowOf(DomainResource.successEmpty()) //skippable?

        return extendedLocations.map { locations ->
            locations?.singleOrNull { location ->
                location.id == extendedLocationId
            }?.let { DomainResource.success(it) }
                ?: DomainResource.successEmpty()
        }
    }
}