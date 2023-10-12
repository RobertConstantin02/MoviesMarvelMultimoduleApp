package com.example.usecase

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
import com.example.resources.DataSourceError
import com.example.resources.RemoteError
import com.example.resources.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

interface UseCaseRemote<Input, Output> {

    suspend fun run(input: Input): Flow<Result<Output>>

    operator fun invoke(
        input: Input,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
        coroutineScope: CoroutineScope? = null,
        success: (Output) -> Unit,
        error: (error: DataSourceError) -> Unit = {}
    ) {
        coroutineScope?.let { scope ->
            val job = scope.async(dispatcher) { run(input) }
            scope.launch(Dispatchers.Main) {
                //try catch here out
                job.await().also { flow ->
                    flow.catch { e -> error(e)
                    }.collectLatest {
                        it.fold(
                            ifLeft = { e -> error(e) },
                            ifRight = { output -> success(output) }
                        )
                    }
                }
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

interface UseCaseLocal<Input, Output> {

    suspend fun run(input: Input): Flow<Output>

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
                job.await().catch { error(it) }.collectLatest { output -> success(output) }
            }
        }
    }
}