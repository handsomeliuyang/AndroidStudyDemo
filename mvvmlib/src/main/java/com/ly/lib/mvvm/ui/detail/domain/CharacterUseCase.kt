package com.ly.lib.mvvm.ui.detail.domain

import com.ly.lib.mvvm.domain.data.CharacterDataSource
import com.ly.lib.mvvm.domain.*
import com.ly.lib.mvvm.domain.entity.Character
import io.reactivex.Observable

class CharacterUseCase(private val source: CharacterDataSource) :
    UseCase<CharacterUseCase.RequestValues, List<Character>>() {

    override fun execute(requestValues: RequestValues?): Observable<Result<List<Character>>> {
        return source.getCharacters(requestValues?.type ?: "")
            .onErrorReturn { it.toErrorResult() }
    }

    class RequestValues(val type: String) : UseCase.RequestValues
}
