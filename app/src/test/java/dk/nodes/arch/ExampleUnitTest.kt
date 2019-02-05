package dk.nodes.arch

import dk.nodes.arch.presentation.base.BasePresenterImpl
import dk.nodes.arch.presentation.base.BaseView
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    private class View: BaseView

    @Test
    fun `Test activating test mode switches contexts to Single thread context`() {
        val basePresenter = object: BasePresenterImpl<View>() {}
        val coroutineContextsBefore = with(basePresenter) {
            listOf(mainCoroutineContext, ioCoroutineContext, defaultCoroutineContext)
        }
        coroutineContextsBefore.forEach {
            assertFalse(it.toString().contains("Single thread context"))
        }

        basePresenter.activateTestMode()

        val coroutineContextsAfter = with(basePresenter) {
            listOf(mainCoroutineContext, ioCoroutineContext, defaultCoroutineContext)
        }
        coroutineContextsAfter.forEach {
            assertTrue(it.toString().contains("Single thread context"))
        }
    }
}