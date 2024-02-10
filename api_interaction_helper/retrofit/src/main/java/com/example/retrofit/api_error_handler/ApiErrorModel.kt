package com.example.retrofit.api_error_handler

import com.google.gson.annotations.SerializedName

data class ApiErrorModel(
    @SerializedName("error")
    val errorMessage: String
)
