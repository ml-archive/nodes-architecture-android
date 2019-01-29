package dk.nodes.arch.presentation.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import java.util.concurrent.LinkedBlockingQueue

abstract class BasePresenterImpl<V> : BasePresenter<V>, LifecycleObserver {

    private val cachedViewActions = LinkedBlockingQueue<Runnable>()
    protected var view: V? = null
    protected var lifecycle: Lifecycle? = null

    override fun onViewCreated(view: V, lifecycle: Lifecycle) {
        this.view = view
        this.lifecycle = lifecycle

        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun onStart() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun onResume() {
        while (!cachedViewActions.isEmpty() && view != null) {
            view?.let { cachedViewActions.poll().run() }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    override fun onPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun onStop() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun onViewDetached() {
        view = null
        cachedViewActions.clear()
        lifecycle?.removeObserver(this)
    }

    fun runAction(runnable: Runnable) {
        if (view != null) {
            view?.let {
                runnable.run()
            }
        } else {
            cachedViewActions.add(runnable)
        }
    }

    fun runAction(action: (V) -> Unit) {
        if (view != null) {
            view?.let {
                action(it)
            }
        } else {
            cachedViewActions.add(Runnable {
                action(view!!)
            })
        }
    }

    fun view(block: V.() -> Unit) {
        view?.let(block) ?: cachedViewActions.add(Runnable { view?.block() })
    }
}