package dk.nodes.arch.presentation.base

import android.arch.lifecycle.Lifecycle


interface BasePresenter<in V> {
    fun onViewAttached(view: V, lifecycle: Lifecycle)

    fun onStart()

    fun onResume()

    fun onPause()

    fun onStop()

    fun onViewDetached()
}