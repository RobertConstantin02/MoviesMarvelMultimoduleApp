package com.example.core.local

import android.content.Context

sealed class DatabaseUnifiedError() {
    data class Insertion(val message: String): DatabaseUnifiedError()
    data class Deletion(val message: String): DatabaseUnifiedError()
    data class Update(val message: String): DatabaseUnifiedError()
    data class Reading(val message: String?): DatabaseUnifiedError()

    companion object {
        fun getError(context: Context):  =

    }

}
