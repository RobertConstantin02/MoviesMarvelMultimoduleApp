package com.example.core.local

import android.content.Context
import com.example.core.R

sealed class DatabaseUnifiedError(val messageResource: Int) {
    object Insertion: DatabaseUnifiedError(R.string.local_db_insertion_error)
    object Deletion: DatabaseUnifiedError(R.string.local_db_deletion_error)
    object Update: DatabaseUnifiedError(R.string.local_db_update_error)
    object Reading: DatabaseUnifiedError(R.string.local_db_read_error)

}
