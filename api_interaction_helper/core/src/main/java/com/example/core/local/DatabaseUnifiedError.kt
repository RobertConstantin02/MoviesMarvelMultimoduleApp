package com.example.core.local

sealed class DatabaseUnifiedError { //pass down messages
    object Insertion: DatabaseUnifiedError()
    object Deletion: DatabaseUnifiedError()
    object Update: DatabaseUnifiedError()
    data class Reading(val message: String?): DatabaseUnifiedError()
}
