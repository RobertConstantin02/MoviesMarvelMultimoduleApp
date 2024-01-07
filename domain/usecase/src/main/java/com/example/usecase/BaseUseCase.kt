package com.example.usecase

import android.util.Log
import arrow.core.left
import com.example.core.remote.Resource
import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction3

interface UseCase<Input, Output> {

    suspend fun run(input: Input): Flow<Resource<Output>>

    operator fun invoke(
        input: Input,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
        coroutineScope: CoroutineScope? = null,
        success: (Output) -> Unit = {},
        error: (String?, Int?, Output?) -> Unit = { _, _, _ ->},
        empty: () -> Unit = {}
    ) {
        coroutineScope?.let { scope ->
            val job = scope.async(dispatcher) { run(input) }
            scope.launch(Dispatchers.Main) {
                try {
                    job.await().also { flow ->
                        flow.catch { e -> error(e) }.collectLatest {
                            when(val resource = it.state) {
                                is Resource.State.Success -> { success(resource.data) }
                                is Resource.State.Error ->
                                    error(resource.apiError, resource.localError, resource.data)
                                is Resource.State.SuccessEmpty -> empty()
                            }

                        }
                    }
                } catch (e: Exception) { error(e) }
            }
        }
    }
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
}

interface FlowUseCase<Input, Output> {
    fun run(input: Input): Flow<Output>
    operator fun invoke(
        params: Input,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
    ) = run(params).flowOn(dispatcher) // TODO: review cast
}