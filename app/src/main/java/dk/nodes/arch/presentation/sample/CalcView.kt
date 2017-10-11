package dk.nodes.arch.presentation.sample

import android.app.Activity
import android.os.Bundle
import dk.nodes.arch.domain.executor.ThreadExecutor
import dk.nodes.arch.domain.interactor.sample.AddTwoNumbersInteractorImpl

/**
 * Created by bison on 11/10/17.
 */
class CalcView : CalcContract.View, Activity() {

    private lateinit var presenter : CalcContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = CalcPresenter(AddTwoNumbersInteractorImpl(ThreadExecutor()))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            presenter.onPresenterDestroyed()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.detachView()
    }

    override fun showResult(result: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}