package com.example.resources

import android.content.Context
import android.content.res.Resources

sealed class UiText {
    data class DynamicText(val id: Int, val code: Int?, val message: String?): UiText()
    data class StringResources(val id: Int): UiText()

    fun asString(context: Context): String =
        when(this) {
            is DynamicText -> String.format(context.getString(id), message, code.toString())
            is StringResources -> context.getString(id)
        }
}
