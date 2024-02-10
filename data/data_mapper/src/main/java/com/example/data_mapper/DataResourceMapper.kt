package com.example.data_mapper

import com.example.core.Resource
import com.example.core.UnifiedError
import com.example.core.local.LocalUnifiedError
import com.example.core.remote.ApiUnifiedError
import com.example.domain_model.error.DomainApiUnifiedError
import com.example.domain_model.error.DomainLocalUnifiedError

import com.example.domain_model.error.DomainUnifiedError

import com.example.domain_model.resource.DomainResource

fun <T> Resource<T>.toDomainResource(): DomainResource<T> =
    when(val state = this.state) {
        is Resource.State.Success -> DomainResource.success(state.data)
        is Resource.State.Error ->
            DomainResource.error(state.error.toDomainUnifiedError(), state.data)
        Resource.State.SuccessEmpty -> DomainResource.successEmpty()
    }

fun UnifiedError.toDomainUnifiedError(): DomainUnifiedError =
    when(val unifiedError = this) {
        is ApiUnifiedError.Generic -> DomainApiUnifiedError.Generic(unifiedError.message)
        is ApiUnifiedError.Http.Unauthorized -> DomainApiUnifiedError.Http.Unauthorized(unifiedError.message, unifiedError.code!!)
        is ApiUnifiedError.Http.NotFound -> DomainApiUnifiedError.Http.NotFound(unifiedError.message, unifiedError.code!!)
        is ApiUnifiedError.Http.InternalErrorApi -> DomainApiUnifiedError.Http.InternalErrorApi(unifiedError.message, unifiedError.code!!)
        is ApiUnifiedError.Http.BadRequest -> DomainApiUnifiedError.Http.BadRequest(unifiedError.message, unifiedError.code!!)
        is ApiUnifiedError.Connectivity.HostUnreachable -> DomainApiUnifiedError.Connectivity.HostUnreachable(unifiedError.message)
        is ApiUnifiedError.Connectivity.TimeOut -> DomainApiUnifiedError.Connectivity.TimeOut(unifiedError.message)
        is ApiUnifiedError.Connectivity.NoConnection -> DomainApiUnifiedError.Connectivity.NoConnection(unifiedError.message)
        is LocalUnifiedError.Insertion -> DomainLocalUnifiedError.Insertion
        is LocalUnifiedError.Deletion -> DomainLocalUnifiedError.Deletion
        is LocalUnifiedError.Update -> DomainLocalUnifiedError.Update
        is LocalUnifiedError.Reading -> DomainLocalUnifiedError.Reading
        else -> DomainApiUnifiedError.Generic(null)
    }
