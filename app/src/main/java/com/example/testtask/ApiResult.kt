package com.example.testtask

sealed interface ApiResult<T>
class ApiSuccess<T>(val data: T) : ApiResult<T>
class ApiError<T>(val message: String?, val code: Int? = null) : ApiResult<T>
class ApiLoading<T> : ApiResult<T>

suspend fun <T> ApiResult<T>.onSuccess(
    executable: suspend (T) -> Unit
) = apply {
    if (this is ApiSuccess<T>) {
        executable(data)
    }
}

suspend fun <T> ApiResult<T>.onLoading(
    executable: suspend () -> Unit
): ApiResult<T> = apply {
    if (this is ApiLoading) {
        executable()
    }
}

suspend fun <T> ApiResult<T>.onError(
    executable: suspend (errorMsg: String) -> Unit
): ApiResult<T> = apply {
    if (this is ApiError<T>) {
        executable(message.orEmpty())
    }
}