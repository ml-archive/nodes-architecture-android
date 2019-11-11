package dk.nodes.arch.presentation

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class NodesFragment : Fragment, HasAndroidInjector {

    constructor(contentResId: Int) : super(contentResId)
    constructor() : super()

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject lateinit var injector: DispatchingAndroidInjector<Any>

    protected inline fun <reified VM : ViewModel> viewModel(): Lazy<VM> =
        viewModels { viewModelFactory }

    protected inline fun <reified VM : ViewModel> activityViewModel(): Lazy<VM> =
        activityViewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun androidInjector(): AndroidInjector<Any>? = injector
}
