package dk.nodes.arch.domain.interactor

interface Interactor<I, O> {
    suspend operator fun invoke(input: I): O
}

suspend operator fun <O> NoInputInteractor<O>.invoke() = invoke(Unit)

typealias NoInputInteractor<O> = Interactor<Unit, O>

typealias UnitInteractor<I> = Interactor<I, Unit>

typealias EmptyInteractor = Interactor<Unit, Unit>