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

    //maybe with abstract class
    //var job: Deferred<Flow<Result<Output>>>?

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
        coroutineScope: CoroutineScope? = null,
        success: (Output) -> Unit,
        error: (error: DataSourceError) -> Unit = {}
    ) {
        //viewModelScope
        coroutineScope?.let { scope ->
            val job = scope.async(dispatcher) { run(input) }
            scope.launch(Dispatchers.Main) {
                //try catch here out
                job?.await().also { flow ->
                    flow?.catch { e ->
                        Log.d("-----> error1", "called")
                        error(e)
                    }?.collectLatest {
                        it.fold(
                            ifLeft = { e ->
                                Log.d("-----> error2", "called")

                                error(e)
                            },
                            ifRight = { output ->
                                Log.d(
                                    "-----> output",
                                    (output as CharacterPresentationScreenBO).toString()
                                )
                                success(output)
                            }
                        )
                    }
                }
            }
        }
    }

    interface Input
    interface Output
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