package com.example.lab6.data.api

sealed class ApiResponse<T> {
    data class Success<T>(val result: T) : ApiResponse<T>()
    data class Error<T>(val error: String) : ApiResponse<T>()
}