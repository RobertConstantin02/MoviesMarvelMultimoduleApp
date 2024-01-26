package com.example.retrofit.api_error_handler

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.R
import com.example.core.remote.UnifiedError
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ApiErrorHandlerImpl2Test {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var apiErrorHandlingUseCaseImpl: ApiErrorHandlerImpl
    @Before
    fun setUp() {
        apiErrorHandlingUseCaseImpl = ApiErrorHandlerImpl(
            context = context
        )
    }

    @Test
    fun `test generic exception`() {
        val throwable = NullPointerException()
        val expectedUnifiedError = UnifiedError.Generic(message = context.getString(R.string.error_generic))
        val unifiedError = apiErrorHandlingUseCaseImpl.invoke(throwable)
        assertEquals(expectedUnifiedError, unifiedError)
    }
}