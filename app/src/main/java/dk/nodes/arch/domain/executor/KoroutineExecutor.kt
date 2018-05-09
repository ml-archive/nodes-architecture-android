package dk.nodes.arch.domain.executor

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Created by bison on 26/07/17.
 */
class KoroutineExecutor : Executor {
    override fun runOnUIThread(code: () -> Unit) {
        launch(UI) {
            code()
        }
    }

    override fun execute(runnable: Runnable) {
        launch(CommonPool) {
            runnable.run()
        }
    }

    override fun sleepUntilSignalled(condId: String, timeout: Long) {
        SignalDispatcher.sleepUntilSignalled(condId, timeout)
    }

    override fun signal(condId: String) {
        SignalDispatcher.signal(condId)
    }
}