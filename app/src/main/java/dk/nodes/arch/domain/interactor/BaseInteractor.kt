package dk.nodes.arch.domain.interactor

import android.util.Log
import dk.nodes.arch.domain.executor.Executor
/**
 * Created by bison on 24-06-2017.
 */

abstract class BaseInteractor(protected val executor: Executor) : Interactor {
    override fun run() {
        executor.execute(Runnable {
            try {
                execute()
            }
            catch (t : Throwable)
            {
                //Log.e("BaseInteractor", "Uncaught throwable in thread ${Thread.currentThread()?.name}")
                //t.printStackTrace()
                submitToHockey(t)
            }
        })
    }

    fun runOnUIThread(code: () -> Unit)
    {
        executor.runOnUIThread(code)
    }

    abstract fun execute()

    fun submitToHockey(t : Throwable)
    {
        val exceptionHandlerCls = Class.forName("net.hockeyapp.android.ExceptionHandler")
        if(exceptionHandlerCls == null)
        {
            Log.e("BaseInteractor", "Could not load HockeyApp SDK Classes, cannot record crash")
        }

        try {
            val default_handler = exceptionHandlerCls.cast(Thread.getDefaultUncaughtExceptionHandler())
            val method = exceptionHandlerCls.getMethod("uncaughtException", Thread::class.java, Throwable::class.java)
            method.invoke(default_handler, Thread.currentThread(), t)
        }
        catch(e : ClassCastException)
        {
            e.printStackTrace()
            Log.e("BaseInteractor", "Could not get HockeySDK uncaught exception handler")
        }
    }
}