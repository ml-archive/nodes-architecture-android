package dk.nodes.arch.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dk.nodes.nstack.kotlin.inflater.NStackBaseContext
import javax.inject.Inject

abstract class NodesActivity : AppCompatActivity, HasAndroidInjector {

    constructor(contentResId: Int) : super() {
        this.contentResId = contentResId
    }
    constructor() : super()

    private var contentResId: Int? = null

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    protected inline fun <reified VM : ViewModel> viewModel(): Lazy<VM> =
        viewModels { viewModelFactory }

    @Inject internal lateinit var injector: DispatchingAndroidInjector<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        contentResId?.let(::setContentView)
        super.onCreate(savedInstanceState)
    }

    override fun androidInjector() = injector

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(NStackBaseContext(newBase))
    }
}