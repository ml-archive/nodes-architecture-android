package dk.nodes.arch.presentation.sample

import dk.nodes.arch.domain.interactor.sample.AddTwoNumbersInteractor
import dk.nodes.arch.presentation.mvp.MvpBasePresenter

/**
 * Created by bison on 11/10/17.
 */
class CalcPresenter(val addTwoNumbersInteractor: AddTwoNumbersInteractor) : CalcContract.Presenter, MvpBasePresenter<CalcContract.View>() {
    override fun attachView(view: CalcContract.View) {
        super.attachView(view)
        addNumbers()
    }

    override fun detachView() {
        super.detachView()
    }

    fun addNumbers()
    {
        // set input
        addTwoNumbersInteractor.setInput(AddTwoNumbersInteractor.Input(20, 30))
        // set output
        addTwoNumbersInteractor.setOutput(object : AddTwoNumbersInteractor.Output {
            override fun onAddTwoNumbersResult(result: Int) {
                if(isViewAttached)
                    view?.showResult(result)
            }
        })
        addTwoNumbersInteractor.run()
    }
}