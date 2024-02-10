package com.example.domain_repository.location

import com.example.core.Resource
import com.example.domain_model.location.ExtendedLocationBo
import com.example.domain_model.resource.DomainResource
import kotlinx.coroutines.flow.Flow

interface ILocationRepository {
    fun getExtendedLocation(extendedLocationId: Int): Flow<DomainResource<ExtendedLocationBo>>
}