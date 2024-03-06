package com.example.usecase

import com.example.domain_model.error.CoroutineError
import com.example.domain_model.error.DomainUnifiedError
import com.example.domain_model.resource.DomainResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

abstract class UseCase<Input, Output> (private val dispatcher: CoroutineDispatcher) {
    abstract suspend fun run(input: Input): Flow<DomainResource<Output>>

    operator fun invoke(
        input: Input,
        coroutineScope: CoroutineScope? = null,
        success: (Output) -> Unit = {},
        error: (e: DomainUnifiedError, data: Output?) -> Unit = { _,_ ->},
        empty: () -> Unit = {}
    ) {
        coroutineScope?.let { scope ->
            scope.launch(Dispatchers.Main) {
                try {
                    run(input).flowOn(dispatcher).catch { e ->
                        error(e.toDomainError(), null)
                    }.collectLatest {
                            when(val resource = it.domainState) {
                                is DomainResource.DomainState.Success -> { success(resource.data) }
                                is DomainResource.DomainState.Error -> error(resource.error, resource.data)
                                is DomainResource.DomainState.SuccessEmpty -> empty()
                            }
                        }
                } catch (e: Exception) { error(e.toDomainError(), null) }
            }
        }
    }

    interface Input
}

private fun Throwable.toDomainError(): DomainUnifiedError = CoroutineError(message)

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

abstract class FlowUseCase<Input, Output>(private val dispatcher: CoroutineDispatcher) {
    abstract fun run(input: Input): Flow<Output>
    operator fun invoke(
        params: Input,
        //dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
    ) = run(params).flowOn(dispatcher)
}