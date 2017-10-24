package dk.nodes.arch.presentation.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent

abstract class BasePresenterImpl<V> : BasePresenter<V>, LifecycleObserver {
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
        lifecycle?.removeObserver(this)
    }
}