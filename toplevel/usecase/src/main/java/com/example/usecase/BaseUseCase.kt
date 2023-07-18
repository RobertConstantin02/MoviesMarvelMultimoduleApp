package com.example.usecase

import com.example.resources.Error
import com.example.resources.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

interface UseCaseRemote<Input, Output> {

    suspend fun run(input: Input):  Flow<Result<Output>>

    /**
     * In the context of coroutines and the Dispatchers.Unconfined dispatcher, the "caller's thread"
     * refers to the thread from which the coroutine was invoked.
     * When a coroutine is launched with Dispatchers.Unconfined, it starts executing in the same
     * thread as the code that called the coroutine. This means that the coroutine runs on the thread
     * that initiated its execution.
     */
    operator fun invoke(
        input: Input,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
        success: (Output) -> Unit,
        error: (error: Error) -> Unit = {}
    ) {
        CoroutineScope(dispatcher).apply {
            val job = async { run(input) }
            launch {
                job.await().catch { e -> error(e) }.collectLatest {
                    it.fold(ifLeft = { e -> error(e) }, ifRight = { output -> success(output) })
                }
            }
        }
    }
}

interface UseCaseLocal<Input, Output> {

    suspend fun run(input: Input): Flow<Output>

    operator fun invoke(
        input: Input,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
        success: (Output) -> Unit,
        error: (error: Error) -> Unit = {}
    ) {
        CoroutineScope(dispatcher).apply {
            val job = async { run(input) }
            launch {
                job.await().catch { error(it) }.collectLatest { output -> success(output) }
            }
        }
    }
}