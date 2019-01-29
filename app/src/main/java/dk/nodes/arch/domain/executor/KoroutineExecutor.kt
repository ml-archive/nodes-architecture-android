package dk.nodes.arch.domain.executor

import dk.nodes.arch.extensions.launch
import dk.nodes.arch.extensions.launchOnUI

class KoroutineExecutor : Executor {

    override fun runOnUIThread(block: () -> Unit) {
        launchOnUI { block() }
    }

    override fun execute(runnable: Runnable) {
        launch { runnable.run() }
    }

    override fun sleepUntilSignalled(condId: String, timeout: Long) {
        SignalDispatcher.sleepUntilSignalled(condId, timeout)
    }

    override fun signal(condId: String) {
        SignalDispatcher.signal(condId)
    }
}