package dk.nodes.arch.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import dk.nodes.nstack.kotlin.inflater.NStackBaseContext
import javax.inject.Inject

abstract class NodesActivity : DaggerAppCompatActivity {

    constructor(contentResId: Int) : super() {
        this.contentResId = contentResId
    }
    constructor() : super()

    private var contentResId: Int? = null

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    protected inline fun <reified VM : ViewModel> viewModel(): Lazy<VM> =
        viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contentResId?.let(::setContentView)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(NStackBaseContext(newBase))
    }
}