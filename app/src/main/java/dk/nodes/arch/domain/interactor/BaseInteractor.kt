package dk.nodes.arch.domain.interactor

import dk.nodes.arch.domain.executor.Executor
/**
 * Created by bison on 24-06-2017.
 */

abstract class BaseInteractor(protected val executor: Executor) : Interactor {
    override fun run() {
        executor.execute(Runnable {
            execute()
        })
    }

    fun runOnUIThread(code: () -> Unit)
    {
        executor.runOnUIThread(code)
    }

    abstract fun execute()
}