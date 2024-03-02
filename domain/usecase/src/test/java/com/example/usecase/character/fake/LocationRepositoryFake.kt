package com.example.usecase.character.fake

import com.example.domain_model.error.DomainApiUnifiedError
import com.example.domain_model.error.DomainUnifiedError
import com.example.domain_model.location.ExtendedLocationBo
import com.example.domain_model.resource.DomainResource
import com.example.domain_repository.location.ILocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

private const val TEST_ERROR_MESSAGE  = "Error Test"
class LocationRepositoryFake: ILocationRepository {
    private val extendedLocations = MutableStateFlow<List<ExtendedLocationBo>?>(emptyList())

    var error: DomainUnifiedError? = null
    var empty: DomainResource.DomainState.SuccessEmpty? = null

    fun setExtendedLocation(locations: List<ExtendedLocationBo>?) {
        this.extendedLocations.value = locations
    }
    override fun getExtendedLocation(extendedLocationId: Int): Flow<DomainResource<ExtendedLocationBo>> {
        if (error != null) return flowOf(DomainResource.error(error!!, extendedLocations.value?.firstOrNull { location -> location.id == extendedLocationId }))
        if (empty != null) return flowOf(DomainResource.successEmpty())

        return extendedLocations.map { locations ->
            locations?.singleOrNull { location ->
                location.id == extendedLocationId
            }?.let { DomainResource.success(it) }
                ?: DomainResource.error(DomainApiUnifiedError.Generic(TEST_ERROR_MESSAGE),  null)
        }
    }
}