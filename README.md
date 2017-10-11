# Nodes architectural
This library contains interfaces and base classes for architectural elements.

## Interactors (Clean architecture)
[Base classes and interfaces](app/src/main/java/dk/nodes/arch/domain/interactor)

### Interactor sample
The sample below simply adds two numbers together.
 
Interactors should be considered use cases operating on pure domain (business) logic. 
Interactors should __not__ directly depend on any classes outside of the domain layer. 
Dependencies should be constructor injected, preferably using a DI
management framework like dagger.

[The interface](app/src/main/java/dk/nodes/arch/domain/interactor/sample/AddTwoNumbersInteractor.kt)
```kotlin
interface AddTwoNumbersInteractor : Interactor {
    fun setOutput(output : Output)
    fun setInput(input: Input)

    /*
        This contain whatever inputs the interactor needs to complete its job, it is set before a call to run()
        by the client (a presenter most likely)
     */
    data class Input (
        val firstNumber : Int = 10,
        val secondNumber : Int = 10
    )

    /*
        This interface is used to communicate results (and errors) back to the client (presenter)
     */
    interface Output {
        fun onAddTwoNumbersResult(result : Int)
    }
}
```

[The implementation](app/src/main/java/dk/nodes/arch/domain/interactor/sample/AddTwoNumbersInteractorImpl.kt)
```kotlin
class AddTwoNumbersInteractorImpl(executor: Executor) : AddTwoNumbersInteractor, BaseInteractor(executor)  {
    private var output : AddTwoNumbersInteractor.Output? = null
    private var input : AddTwoNumbersInteractor.Input? = null

    override fun setOutput(output: AddTwoNumbersInteractor.Output) {
        this.output = output
    }

    override fun setInput(input: AddTwoNumbersInteractor.Input) {
        this.input = input
    }

    /*
        Remember this badboy typically runs on a background thread
        use runOnUiThread to deliver results
     */
    override fun execute() {
        val result = (input?.firstNumber ?: 0) + (input?.secondNumber ?: 0)
        runOnUIThread {
            output?.onAddTwoNumbersResult(result)
        }
    }
}
```

#### The Executor
The executor is responsible for executing your interactors. The executor is an [interface](app/src/main/java/dk/nodes/arch/domain/executor/Executor.kt) and different implementations are
available:

- [ThreadPool executor](app/src/main/java/dk/nodes/arch/domain/executor/ThreadExecutor.kt) Uses JDK ThreadPoolExecutor
- [Coroutine executor](app/src/main/java/dk/nodes/arch/domain/executor/KoroutineExecutor.kt) Uses kotlin coroutines
- [Test executor](app/src/main/java/dk/nodes/arch/domain/executor/TestExecutor.kt) This runs everything on the mainthread, useful for testing.


## MVP (Model-View-Presenter)
[Base classes and interfaces](app/src/main/java/dk/nodes/arch/presentation/mvp)

### Usage
Following demonstrates the basic usage of the MVP baseclasses. In real working code you should inject dependencies with a
dependency injection framework like dagger or factory methods (providers).

Our example - CalcView - is a view which presents the result of two numbers being added together (exciting stuff) 

#### The Contract
This file contain the abstract interfaces for the view and the presenter, in a neat parent class. The interfaces
could just as well be split into two files depending on preference.

Source: [CalcContract.kt](app/src/main/java/dk/nodes/arch/presentation/sample/CalcContract.kt)

```kotlin
interface CalcContract {
    interface View : MvpView {
        fun showResult(result : Int)
    }
    interface Presenter : MvpPresenter<CalcContract.View> {
    }
}
```

#### The View
The view implementation in this sample shows the results of the addition of two numbers, in some way
we don't care about. In Android this class is usually an Activity or a Fragment (or another class derived from ViewGroup).
The interactor and executor should be injected or instantiated with a factory function in production code.

Source: [CalcView.kt](app/src/main/java/dk/nodes/arch/presentation/sample/CalcView.kt)

```kotlin
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
```

#### The Presenter
The Presenter implementation is derived from the interface in it's contract (CalcContract.Presenter) and MvpBasePresenter for some
general plumbing and providing default implementations of onAttach and onDetach. The Interactor is constructor injected so it
can easily be mocked in a test situation.

The addNumbers function uses the interactor in the earlier example two perform the cutting edge addition of two integers in
a background task. The result of this intensive number crunching is then delivered with an anonymous object implementing the interactor
output. The presenter itself could also have derived the AddTwoNumbersInteractor.Output interface to avoid the closure.

Source: [CalcPresenter.kt](app/src/main/java/dk/nodes/arch/presentation/sample/CalcPresenter.kt)

```kotlin
class CalcPresenter(val addTwoNumbersInteractor: AddTwoNumbersInteractor) : CalcContract.Presenter, MvpBasePresenter<CalcContract.View>() {
    override fun attachView(view: CalcContract.View) {
        super.attachView(view)
        addNumbers()
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
        // schedules and runs the interactor in the background
        addTwoNumbersInteractor.run()
    }
}
```

## Gradle Dependency
```groovy
dependencies {
	compile 'something'
}
```
