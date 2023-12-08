package com.example.retrofit.adapter

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @T : Original repsonse from server
 * @R : Transformed Response
 * @proxy : This property holds the original Call object of type T that the delegate is wrapping.
 *
 * enqueueImpl(callback: Callback<R>): This abstract method is responsible for
 * implementing the logic of how to enqueue the original proxy Call object and handle the response.
 *
 * CallWrapper acts as a customizable wrapper around a Retrofit Call original object, allowing for
 * transformations and modifications to the behavior of the original call. CallTypeAdapter specifically
 * extends CallDelegate to transform the response into an ApiResponse<T>. This can be useful for
 * handling API responses in a standardized way across your application.
 */
internal abstract class CallWrapper<T, R>(
    protected val proxy: Call<T>
) : Call<R>  {
    override fun enqueue(callback: Callback<R>) = enqueueImpl(callback)
    override fun execute(): Response<R> = throw NotImplementedError()
    override fun clone(): Call<R> = cloneImpl()

    override fun cancel() = proxy.cancel()
    override fun request(): Request = proxy.request()
    override fun timeout(): Timeout = proxy.timeout()
    override fun isExecuted(): Boolean = proxy.isExecuted
    override fun isCanceled(): Boolean = proxy.isCanceled

    abstract fun enqueueImpl(callback: Callback<R>)
    abstract fun cloneImpl(): Call<R>
}