package dk.nodes.arch.presentation.mvp

import java.lang.ref.WeakReference
import java.util.concurrent.LinkedBlockingQueue


/**
 * Created by joso on 06/09/16.
 * Implementation from: https://github.com/sockeqwe/mosby/blob/master/mvp-common/src/main/java/com/hannesdorfmann/mosby/mvp/MvpBasePresenter.java
 * And inspiration from: https://github.com/grandcentrix/ThirtyInch/blob/master/thirtyinch/src/main/java/net/grandcentrix/thirtyinch/TiPresenter.java
 */
open class MvpBasePresenter<V : MvpView> : MvpPresenter<V> {
    override fun onPresenterDestroyed() {
        cachedViewActions.clear()
    }

    private val cachedViewActions = LinkedBlockingQueue<ViewAction<V>>()
    private var weakReference: WeakReference<V>? = null

    override fun attachView(view: V) {
        weakReference = WeakReference(view)
        while (!cachedViewActions.isEmpty()) {
            cachedViewActions.poll().run(view)
        }
    }

    override fun detachView() {
        weakReference?.clear()
        weakReference = null
    }

    fun viewAction(action: ViewAction<V>) {
        if(isViewAttached) {
            view?.let {
                action.run(it)
            }
        } else {
            cachedViewActions.add(action)
        }
    }


    // custom getter/setter wrapping the weakreference, equivalent to function imp above
    val view: V?
        get() = weakReference?.get()

    val isViewAttached: Boolean
        get() = weakReference?.get() != null


}

