package dk.nodes.arch.extensions

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.exceptions.Exceptions
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

fun <T> Call<T>.toDeferred(): Deferred<T> {
    val deferred = CompletableDeferred<T>()
    deferred.invokeOnCompletion {
        if (deferred.isCancelled) {
            cancel()
        }
    }

    enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            deferred.completeExceptionally(t)
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                deferred.complete(response.body()!!)
            } else {
                deferred.completeExceptionally(HttpException(response))
            }
        }
    })
    return deferred
}

fun <T> Call<T>.toDeferredResponse(): Deferred<Response<T>> {
    val deferred = CompletableDeferred<Response<T>>()

    deferred.invokeOnCompletion {
        if (deferred.isCancelled) {
            cancel()
        }
    }

    enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            deferred.completeExceptionally(t)
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            deferred.complete(response)
        }
    })

    return deferred
}

fun <T> Call<T>.toRxObservable(): Observable<T> = BodyObservable(RetrofitCallObservable(this))
fun <T> Call<T>.toRxSingle(): Single<T> = toRxObservable().singleOrError()
fun <T> Call<T>.toRxMaybe(): Maybe<T> = toRxObservable().singleElement()
fun <T> Call<T>.toRxFlowable(): Flowable<T> = toRxObservable().toFlowable()

private class RetrofitCallObservable<T>(private val originalCall: Call<T>) :
    Observable<Response<T>>() {

    override fun subscribeActual(observer: Observer<in Response<T>>) {
        // Since Call is a one-shot type, clone it for each new observer.
        val call = originalCall.clone()
        observer.onSubscribe(CallDisposable(call))

        var terminated = false
        try {
            val response = call.execute()
            if (!call.isCanceled) {
                observer.onNext(response)
            }
            if (!call.isCanceled) {
                terminated = true
                observer.onComplete()
            }
        } catch (t: Throwable) {
            Exceptions.throwIfFatal(t)
            if (terminated) {
                RxJavaPlugins.onError(t)
            } else if (!call.isCanceled) {
                try {
                    observer.onError(t)
                } catch (inner: Throwable) {
                    Exceptions.throwIfFatal(inner)
                    RxJavaPlugins.onError(CompositeException(t, inner))
                }
            }
        }
    }
}

private class CallDisposable(private val call: Call<*>) : Disposable {
    override fun dispose() {
        call.cancel()
    }

    override fun isDisposed(): Boolean {
        return call.isCanceled
    }
}

private class BodyObservable<T>(private val upstream: Observable<Response<T>>) : Observable<T>() {
    override fun subscribeActual(observer: Observer<in T>) {
        upstream.subscribe(BodyObserver(observer))
    }
}

private class BodyObserver<R>(private val observer: Observer<in R>) : Observer<Response<R>> {
    private var terminated: Boolean = false

    override fun onSubscribe(disposable: Disposable) {
        observer.onSubscribe(disposable)
    }

    override fun onNext(response: Response<R>) {
        if (response.isSuccessful) {
            observer.onNext(response.body()!!)
        } else {
            terminated = true
            val t = HttpException(response)
            try {
                observer.onError(t)
            } catch (inner: Throwable) {
                Exceptions.throwIfFatal(inner)
                RxJavaPlugins.onError(CompositeException(t, inner))
            }
        }
    }

    override fun onComplete() {
        if (!terminated) {
            observer.onComplete()
        }
    }

    override fun onError(throwable: Throwable) {
        if (!terminated) {
            observer.onError(throwable)
        } else {
            // This should never happen! onNext handles and forwards errors automatically.
            val broken = AssertionError(
                "This should never happen! Report as a bug with the full stacktrace."
            )

            broken.initCause(throwable)
            RxJavaPlugins.onError(broken)
        }
    }
}

fun <T> Call<T>.fetchBody(): T = execute().bodyOrThrow()

fun <T> Response<T>.bodyOrThrow(): T {
    if (!isSuccessful) throw HttpException(this)
    return body()!!
}

fun <T> Response<T>.toException() = HttpException(this)

suspend inline fun <T> Call<T>.executeWithRetry(
    firstDelay: Long = 100,
    maxAttempts: Int = 3,
    shouldRetry: (Exception) -> Boolean = ::defaultShouldRetry
): Response<T> {
    var nextDelay = firstDelay
    repeat(maxAttempts - 1) { attempt ->
        try {
            // Clone a new ready call if needed
            val call = if (isExecuted) clone() else this
            return call.execute()
        } catch (e: Exception) {
            // The response failed, so lets see if we should retry again
            if (attempt == (maxAttempts - 1) || !shouldRetry(e)) {
                throw e
            }
        }
        // Delay to implement exp. backoff
        delay(nextDelay)
        // Increase the next delay
        nextDelay *= 2
    }

    // We should never hit here
    throw IllegalStateException("Unknown exception from executeWithRetry")
}

suspend inline fun <T> Call<T>.fetchBodyWithRetry(
    firstDelay: Long = 100,
    maxAttempts: Int = 3,
    shouldRetry: (Exception) -> Boolean = ::defaultShouldRetry
) = executeWithRetry(firstDelay, maxAttempts, shouldRetry).bodyOrThrow()

fun defaultShouldRetry(exception: Exception) = when (exception) {
    is HttpException -> exception.code() == 429
    is IOException -> true
    else -> false
}
