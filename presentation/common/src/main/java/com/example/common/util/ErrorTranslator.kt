package com.example.common.util

import com.example.common.R
import com.example.common.screen.ScreenStateEvent
import com.example.domain_model.error.DomainApiUnifiedError
import com.example.domain_model.error.DomainLocalUnifiedError
import com.example.domain_model.error.DomainUnifiedError
import com.example.resources.UiText

fun <T> translateError(error: DomainUnifiedError, data: T?) =
    when(error) {
        is DomainApiUnifiedError -> {
            val localString = when(error) {
                is DomainApiUnifiedError.Connectivity.HostUnreachable -> R.string.connectivity_error_host_unreachable
                is DomainApiUnifiedError.Connectivity.NoConnection -> R.string.connectivity_error_no_connection
                is DomainApiUnifiedError.Connectivity.TimeOut -> R.string.connectivity_error_timeout
                is DomainApiUnifiedError.Generic -> R.string.error_generic
                is DomainApiUnifiedError.Http.BadRequest -> R.string.http_error_bad_request
                is DomainApiUnifiedError.Http.EmptyResponse -> R.string.http_error_empty_response
                is DomainApiUnifiedError.Http.InternalErrorApi -> R.string.http_error_internal
                is DomainApiUnifiedError.Http.NotFound -> R.string.http_error_not_found
                is DomainApiUnifiedError.Http.Unauthorized -> R.string.http_error_unauthorized
            }
            ScreenStateEvent.OnError(UiText.DynamicText(localString, error.code, error.message), data)
        }
        is DomainLocalUnifiedError -> {
            val localError = when(error) {
                is DomainLocalUnifiedError.Insertion -> R.string.local_db_insertion_error
                is DomainLocalUnifiedError.Deletion -> R.string.local_db_deletion_error
                is DomainLocalUnifiedError.Update -> R.string.local_db_update_error
                is DomainLocalUnifiedError.Reading -> R.string.local_db_read_error
            }
            ScreenStateEvent.OnError(UiText.StringResources(localError), null)
        }
        else ->
            ScreenStateEvent.OnError(UiText.StringResources(R.string.error_generic), null)
    }