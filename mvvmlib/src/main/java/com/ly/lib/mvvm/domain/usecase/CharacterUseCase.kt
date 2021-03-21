package com.ly.lib.mvvm.domain.usecase

import com.ly.lib.mvvm.domain.entity.Character
import com.ly.lib.mvvm.domain.executor.PostExecutionThread
import com.ly.lib.mvvm.domain.executor.ThreadExecutor
import com.ly.lib.mvvm.domain.repository.CharacterRepository
import io.reactivex.Observable

class CharacterUseCase(
    private val repository: CharacterRepository,
    private val threadExecutor: ThreadExecutor,
    private val postExecutionThread: PostExecutionThread
) : UseCase<CharacterUseCase.RequestValues, List<Character>>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseObservable(requestValues: RequestValues?): Observable<Result<List<Character>>> {
        return repository.getCharacters(requestValues?.type ?: "")
            .onErrorReturn { it.toErrorResult() }
    }

    class RequestValues(val type: String) : UseCase.RequestValues
}
