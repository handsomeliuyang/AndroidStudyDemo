package com.ly.lib.mvvm.data.repository.datasource

import com.ly.lib.mvvm.domain.usecase.Result
import com.ly.lib.mvvm.domain.usecase.toErrorResult
import com.ly.lib.mvvm.domain.usecase.toResult
import com.ly.lib.mvvm.domain.entity.Character
import io.reactivex.Observable

class LocalCharacterDataSource : CharacterDataSource {

    private var data: MutableList<Character> = mutableListOf()

    override fun getCharacters(type: String): Observable<Result<List<Character>>> {
        return Observable.create<List<Character>> { emitter ->
                if (data.size > 0) {
                    emitter.onNext(data)
                }
                emitter.onComplete()
            }
            .map<Result<List<Character>>> { it.toResult() }
            .onErrorReturn { it.toErrorResult() }
    }

    fun saveCharacters(list: List<Character>) {
        data.clear()
        data.addAll(list)
    }
}