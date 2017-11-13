package dk.nodes.arch.presentation.mvp

/**
 * Created by johnny on 17/10/2017.
 */

interface ViewAction<V : MvpView> {
    fun run(v: V)
}