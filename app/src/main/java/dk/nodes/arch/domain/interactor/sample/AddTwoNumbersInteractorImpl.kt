package dk.nodes.arch.domain.interactor.sample

internal class AddTwoNumbersInteractorImpl : AddTwoNumbersInteractor {

    override suspend fun invoke(input: AddTwoNumbersInteractor.Input): AddTwoNumbersInteractor.Output {
        return AddTwoNumbersInteractor.Output(input.firstNumber + input.secondNumber)
    }
}

