package dk.nodes.arch.presentation.sample

import dk.nodes.arch.presentation.mvp.MvpPresenter
import dk.nodes.arch.presentation.mvp.MvpView

/**
 * Created by bison on 11/10/17.
 */
interface CalcContract {
    interface View : MvpView {
        fun showResult(result : Int)
    }
    interface Presenter : MvpPresenter<CalcContract.View> {

    }
}