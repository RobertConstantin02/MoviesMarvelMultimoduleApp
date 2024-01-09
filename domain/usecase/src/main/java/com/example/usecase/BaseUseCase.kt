package com.example.usecase

import com.example.core.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import com.example.core.R
import com.example.domain_model.error.DomainError
import kotlin.Error

interface UseCase<Input, Output> {

    suspend fun run(input: Input): Flow<Resource<Output>>

    operator fun invoke(
        input: Input,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
        coroutineScope: CoroutineScope? = null,
        success: (Output) -> Unit = {},
        error: (e: DomainError<Output>) -> Unit = { _ ->},
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
                                    resource.apiError?.let { apiError ->
                                        error(DomainError.ApiError(apiError, resource.data))
                                    } ?: error(DomainError.LocalError<Unit>(resource.localError ?: R.string.error_generic))
                                is Resource.State.SuccessEmpty -> empty()
                            }
                        }
                    }
                } catch (e: Exception) { error(e) }
            }
        }
    }

    interface Input
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

interface FlowUseCase<Input, Output> {
    fun run(input: Input): Flow<Output>
    operator fun invoke(
        params: Input,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
    ) = run(params).flowOn(dispatcher)
}