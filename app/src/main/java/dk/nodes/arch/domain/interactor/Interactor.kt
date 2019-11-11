package dk.nodes.arch.domain.interactor

interface Interactor<I, O> {
    suspend operator fun invoke(input: I): O
}

suspend operator fun <O> NoInputInteractor<O>.invoke() = invoke(Unit)

interface NoInputInteractor<O> : Interactor<Unit, O>

interface NoOutputInteractor<I> : Interactor<I, Unit>

interface EmptyInteractor : Interactor<Unit, Unit>