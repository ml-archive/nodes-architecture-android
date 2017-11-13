package dk.nodes.arch.presentation.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import dk.nodes.arch.presentation.mvp.ViewAction
import java.util.concurrent.LinkedBlockingQueue

abstract class BasePresenterImpl<V> : BasePresenter<V>, LifecycleObserver {
    private val cachedViewActions = LinkedBlockingQueue<ViewAction<V>>()
    protected var view: V? = null
    protected var lifecycle: Lifecycle? = null

    override fun onViewAttached(view: V, lifecycle: Lifecycle) {
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
            view?.let { cachedViewActions.poll().run(it) }
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
        cachedViewActions?.clear()
        lifecycle?.removeObserver(this)
    }

    fun run(action: ViewAction<V>) {
        if (view != null) {
            view?.let {
                action.run(it)
            }
        } else {
            cachedViewActions.add(action)
        }
    }
}