package com.example.core.local

import com.example.core.UnifiedError

sealed class LocalUnifiedError: UnifiedError {
//    object Insertion: DatabaseUnifiedError(R.string.local_db_insertion_error)
//    object Deletion: DatabaseUnifiedError(R.string.local_db_deletion_error)
//    object Update: DatabaseUnifiedError(R.string.local_db_update_error)
//    object Reading: DatabaseUnifiedError(R.string.local_db_read_error)
    object Insertion: LocalUnifiedError()
    object Deletion: LocalUnifiedError()
    object Update: LocalUnifiedError()
    object Reading: LocalUnifiedError()
}
