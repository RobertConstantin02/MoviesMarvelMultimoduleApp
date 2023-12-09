package com.example.retrofit.facotry.extension

import com.example.core.implement.Response

/**
 * If you need to switch to a different networking library or make changes to how you handle
 * responses, you can do so more easily. You only need to update the extension function and the
 * common response interface, keeping the rest of your code unchanged.
 */
internal fun <T> retrofit2.Response<T>.mapToCommonResponse(): Response<T> =
    object : Response<T> {
        override val isSuccessful: Boolean
            get() = this@mapToCommonResponse.isSuccessful
        override val code: Int
            get() = this@mapToCommonResponse.code()
        override val errorDescription: String
            get() = this@mapToCommonResponse.errorBody()?.string() ?: this@mapToCommonResponse.message()

        override fun body(): T? = this@mapToCommonResponse.body()

        override fun headers(): Set<Map.Entry<String, List<String>>> =
            this@mapToCommonResponse.headers().toMultimap().entries
    }