package dk.nodes.arch.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import dk.nodes.arch.extensions.getSharedViewModel
import dk.nodes.arch.extensions.getViewModel
import dk.nodes.arch.extensions.lifecycleAwareLazy
import javax.inject.Inject

abstract class NodesFragment : DaggerFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    protected inline fun <reified VM : ViewModel> getViewModel(): VM =
        getViewModel(viewModelFactory)

    protected inline fun <reified VM : ViewModel> getSharedViewModel(): VM =
        getSharedViewModel(viewModelFactory)

    protected inline fun <reified VM : ViewModel> viewModel(): Lazy<VM> = lifecycleAwareLazy(this) {
        getViewModel<VM>()
    }

    protected inline fun <reified VM : ViewModel> sharedViewModel(): Lazy<VM> =
        lifecycleAwareLazy(this) {
            getSharedViewModel<VM>()
        }
}
