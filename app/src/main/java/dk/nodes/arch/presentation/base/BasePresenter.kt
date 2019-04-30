package dk.nodes.arch.presentation.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlin.coroutines.CoroutineContext

interface BasePresenter<in V> {

    @Deprecated(
        "For fragments use Fragment.getViewLifecycleOwner()",
        replaceWith = ReplaceWith("onCreate(view: V, lifecycleOwner: LifecycleOwner)")
    )
    fun onCreate(view: V, lifecycle: Lifecycle)

    @Deprecated(
        "For fragments use Fragment.getViewLifecycleOwner()",
        replaceWith = ReplaceWith("onViewCreated(view: V, lifecycleOwner: LifecycleOwner)")
    )
    fun onViewCreated(view: V, lifecycle: Lifecycle)

    fun onCreate(view: V, lifecycleOwner: LifecycleOwner)

    fun onViewCreated(view: V, lifecycleOwner: LifecycleOwner)

    fun onStart()

    fun onResume()

    fun onPause()

    fun onStop()

    fun onViewDetached()

    fun activateTestMode(context: CoroutineContext)
}