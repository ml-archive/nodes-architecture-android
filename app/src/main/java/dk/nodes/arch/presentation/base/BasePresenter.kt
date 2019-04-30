package dk.nodes.arch.presentation.base

import androidx.lifecycle.LifecycleOwner
import kotlin.coroutines.CoroutineContext

interface BasePresenter<in V> {

    fun onCreate(view: V, lifecycleOwner: LifecycleOwner)

    fun onViewCreated(view: V, lifecycleOwner: LifecycleOwner)

    fun onStart()

    fun onResume()

    fun onPause()

    fun onStop()

    fun onViewDetached()

    fun activateTestMode(context: CoroutineContext)
}