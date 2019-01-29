package dk.nodes.arch.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

suspend fun delay(time: Long, timeUnit: TimeUnit) {
    kotlinx.coroutines.delay(timeUnit.toMillis(time))
}

fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit
) = GlobalScope.launch(context = context, block = block)

fun launchOnUI(block: suspend CoroutineScope.() -> Unit) =
    launch(context = Dispatchers.Main, block = block)

fun launchOnIO(block: suspend CoroutineScope.() -> Unit) =
    launch(context = Dispatchers.IO, block = block)

fun <T> async(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T
) = GlobalScope.async(context = context, block = block)

fun <T> asyncOnUI(block: suspend CoroutineScope.() -> T) =
    async(context = Dispatchers.Main, block = block)

fun <T> asyncOnIO(block: suspend CoroutineScope.() -> T) =
    async(context = Dispatchers.IO, block = block)
