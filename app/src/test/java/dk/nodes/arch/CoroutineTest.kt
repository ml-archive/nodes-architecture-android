package dk.nodes.arch

import androidx.lifecycle.Lifecycle
import dk.nodes.arch.presentation.base.BasePresenterImpl
import dk.nodes.arch.presentation.base.BaseView
import dk.nodes.arch.presentation.base.runBlockingTest
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import org.junit.Test

class CoroutineTest {

    private class View: BaseView {
        fun showData() {
            println("Showing data")
        }
    }

    private class Presenter: BasePresenterImpl<View>() {

        fun loadData() {
            println("loadData start in thread ${Thread.currentThread().name}")
            launchOnUI {
                println("loadData before delay in thread ${Thread.currentThread().name}")
                delay(1000)
                println("loadData after delay in thread ${Thread.currentThread().name}")
                view { showData() }
                println("loadData after view in thread ${Thread.currentThread().name}")
            }
            println("loadData end in thread ${Thread.currentThread().name}")
        }
    }

    @InternalCoroutinesApi
    @Test
    fun `Test run blocking with context launches coroutine on same thread`() {
        val viewMock  = mockk<View>(relaxed = true)
        val lifecycleMock  = mockk<Lifecycle>(relaxed = true)
        val presenter = Presenter()

        presenter.onViewCreated(viewMock, lifecycleMock)

        runBlockingTest(presenter) {
            presenter.loadData()
        }

        verify { viewMock.showData() }
    }
}