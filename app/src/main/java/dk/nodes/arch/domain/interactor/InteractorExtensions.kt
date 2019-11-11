package dk.nodes.arch.domain.interactor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface ResultInteractor<I, O> : Interactor<I, CompleteResult<O>>

interface FlowInteractor<I, O> : NoOutputInteractor<I> {
    fun flow(): Flow<InteractorResult<O>>
}

class ResultInteractorImpl<I, O>(private val interactor: Interactor<I, out O>) :
    ResultInteractor<I, O> {
    override suspend fun invoke(input: I): CompleteResult<O> {
        return try {
            Success(interactor(input))
        } catch (t: Throwable) {
            Fail(t)
        }
    }
}

class FlowInteractorImpl<I, O>(private val interactor: Interactor<I, out O>) :
    FlowInteractor<I, O> {

    private val channel = ConflatedBroadcastChannel<InteractorResult<O>>(Uninitialized)

    override fun flow(): Flow<InteractorResult<O>> {
        return channel.asFlow()
    }

    override suspend fun invoke(input: I) {
        channel.offer(Loading())
        try {
            channel.offer(Success(interactor(input)))
        } catch (t: Throwable) {
            channel.offer(Fail(t))
        }
    }
}

fun <I, O> Interactor<I, O>.asResult(): ResultInteractor<I, O> {
    return ResultInteractorImpl(this)
}

fun <I, O> Interactor<I, O>.asFlow(): FlowInteractor<I, O> {
    return FlowInteractorImpl(this)
}

fun <I> CoroutineScope.launchInteractor(
    interactor: Interactor<I, Unit>,
    input: I,
    coroutineContext: CoroutineContext = Dispatchers.IO
) {
    launch(coroutineContext) { interactor(input) }
}

fun CoroutineScope.launchInteractor(
    interactor: Interactor<Unit, Unit>,
    coroutineContext: CoroutineContext = Dispatchers.IO
) {
    launchInteractor(interactor, Unit, coroutineContext)
}

suspend fun <I, T> runInteractor(
    interactor: Interactor<I, T>,
    input: I,
    coroutineContext: CoroutineContext = Dispatchers.IO
): T {
    return withContext(coroutineContext) { interactor(input) }
}

suspend fun <O> runInteractor(
    interactor: NoInputInteractor<O>,
    coroutineContext: CoroutineContext = Dispatchers.IO
): O {
    return withContext(coroutineContext) { interactor.invoke() }
}

fun <T, R> InteractorResult<T>.isSuccess(block: (T) -> R): InteractorResult<T> {
    if (this is Success) {
        block(this.data)
    }
    return this
}

fun <T, R> InteractorResult<T>.isError(block: (throwable: Throwable) -> R): InteractorResult<T> {
    if (this is Fail) {
        block(this.throwable)
    }
    return this
}