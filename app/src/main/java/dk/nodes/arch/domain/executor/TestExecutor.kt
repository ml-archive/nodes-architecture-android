package dk.nodes.arch.domain.executor

/**
 * Created by bison on 26/07/17.
 * Singlethreaded executer used for unit testing interactors
 */
class TestExecutor : Executor {
    override fun runOnUIThread(code: () -> Unit) {
        code()
    }

    override fun execute(runnable: Runnable) {
        runnable.run()
    }

    override fun sleepUntilSignalled(condId: String, timeout: Long) {
        return
    }

    override fun signal(condId: String) {}
}