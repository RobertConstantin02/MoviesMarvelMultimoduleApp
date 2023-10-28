package com.example.usecase

import com.example.resources.DataSourceError
import com.example.resources.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

interface UseCase<Input, Output> {

    suspend fun run(input: Input): Flow<Result<Output>>

    operator fun invoke(
        input: Input,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
        coroutineScope: CoroutineScope? = null,
        success: (Output) -> Unit,
        error: (error: DataSourceError) -> Unit = {} //review here and put something more gerneric because of the catch
    ) {
        coroutineScope?.let { scope ->
            val job = scope.async(dispatcher) { run(input) }
            scope.launch(Dispatchers.Main) {
                try {
                    job.await().also { flow ->
                        flow.catch { e -> error(e) }.collectLatest {
                            it.fold(
                                ifLeft = { e -> error(e) },
                                ifRight = { output -> success(output) }
                            )
                        }
                    }
                } catch (e: Exception) { error(e) }
            }
        }
    }

    interface Input
    interface OutPut
}

interface UseCaseNoOutput<Input> {
    suspend fun run(input: Input)
    operator fun invoke(
        input: Input,
        coroutineScope: CoroutineScope? = null,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
        success: () -> Unit = {},
        error: (error: Error) -> Unit = {}
    ) {
        coroutineScope?.let { scope ->
            val job = scope.async(dispatcher) { run(input) }
            scope.launch {
                try {
                    job.await()
                    success()
                }catch (e: Exception) { error(e) }
            }
        }
    }

    interface Input
}

// TODO: try to put here the result to handle posible exceptions
interface UseCaseLocal<Input, Output> {

    fun run(input: Input): Flow<Output>

    operator fun invoke(
        input: Input,
        coroutineScope: CoroutineScope? = null,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
        success: (Output) -> Unit,
        error: (error: Error) -> Unit = {}
    ) {
        coroutineScope?.let { scope ->
            val job = scope.async(dispatcher) { run(input) }
            scope.launch {
                job.await().also { flow ->
                    flow.catch { error(it) }.collectLatest { output -> success(output) }
                }
            }
        }
    }

    interface Input
    interface Output
}

interface PagingUseCase<Input, Output> {
    fun run(input: Input): Flow<Output>
    operator fun invoke(
        params: Input,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
    ) = run(params).catch { emit(it as Output) }.flowOn(dispatcher)

}