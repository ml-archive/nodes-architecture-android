package dk.nodes.arch.presentation.base

import androidx.lifecycle.Lifecycle

interface BasePresenter<in V> {

    fun onCreate()

    fun onCreateView(lifecycle: Lifecycle)

    fun onViewCreated(view: V, lifecycle: Lifecycle)

    fun onStart()

    fun onResume()

    fun onPause()

    fun onStop()

    fun onViewDetached()
}