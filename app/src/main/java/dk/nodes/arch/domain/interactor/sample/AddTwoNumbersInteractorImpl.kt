package dk.nodes.arch.domain.interactor.sample

import dk.nodes.arch.domain.executor.Executor
import dk.nodes.arch.domain.interactor.BaseInteractor

/**
 * Created by bison on 10/10/17.
 */
class AddTwoNumbersInteractorImpl(executor: Executor) : AddTwoNumbersInteractor, BaseInteractor(executor) {
    private var output: AddTwoNumbersInteractor.Output? = null
    private var input: AddTwoNumbersInteractor.Input? = null

    override fun setOutput(output: AddTwoNumbersInteractor.Output) {
        this.output = output
    }

    override fun setInput(input: AddTwoNumbersInteractor.Input) {
        this.input = input
    }

    /*
        Remember this badboy typically runs on a background thread
        use runOnUiThread to deliver results
     */
    override fun execute() {
        val result = (input?.firstNumber ?: 0) + (input?.secondNumber ?: 0)
        runOnUIThread {
            output?.onAddTwoNumbersResult(result)
        }
    }
}

