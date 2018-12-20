# Nodes architectural
This library contains interfaces and base classes for architectural elements.

## New in version 3.0.0

### Signal dispatch
Simple signal dispatch feature. Interactor can now ask its executor to sleep until a signal arrive.

Image your interactor is running but need to write to external storage. Permissions requests are an
asynchronous operation, but it should happen immediately before the permission is needed.
An example use of the signal dispatch feature could be for the interactor to wait for a permission
request to complete.

To go to sleep in the interactor:

```kotlin
executor.sleepUntilSignalled("permissionRequestDone")
```

Specify an optional timeout in ms as the second parameter.
Whenever you want the interactor to wake up and resume its operation

```kotlin
executor.signal("permissionRequestDone")
// interactor woke up, check the result of whatever we were waiting for and continuef
```

### guard()
It's the opposite of let:

```kotlin
nullableVar?.guard {
	myFancyErrorHandling()
}
```


## Usage
  * [Interactors (Clean architecture)](#interactors-clean-architecture)
	 * [Interactor sample](#interactor-sample)
		* [The Executor](#the-executor)
  * [Gradle Dependency](#gradle-dependency)


## Interactors (Clean architecture)
[Base classes and interfaces](app/src/main/java/dk/nodes/arch/domain/interactor)

### Sample usage
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

## Gradle Dependency
```groovy
dependencies {
	compile 'something'
}
```
