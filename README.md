# Nodes architectural
This library contains interfaces and base classes for architectural elements.

## MVP (Model-View-Presenter)
[Base classes and interfaces](app/src/main/java/dk/nodes/arch/presentation/mvp)

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

## Gradle dependency
```groovy
dependencies {
	compile 'something'
}
```
