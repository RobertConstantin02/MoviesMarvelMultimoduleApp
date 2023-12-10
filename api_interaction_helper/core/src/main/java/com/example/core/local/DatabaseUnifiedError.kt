package com.example.core.local

import android.content.Context

sealed class DatabaseUnifiedError(message: String) {
    object Insertion: DatabaseUnifiedError()
    object Deletion: DatabaseUnifiedError()
    object Update: DatabaseUnifiedError()
    data class Reading(val message: String?): DatabaseUnifiedError()

    companion object {
        fun getError(context: Context):  =

    }

}
