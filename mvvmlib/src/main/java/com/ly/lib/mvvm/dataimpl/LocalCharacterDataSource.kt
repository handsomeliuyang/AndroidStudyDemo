package com.ly.lib.mvvm.dataimpl

import com.ly.lib.mvvm.domain.data.CharacterDataSource
import com.ly.lib.mvvm.domain.Result
import com.ly.lib.mvvm.domain.toErrorResult
import com.ly.lib.mvvm.domain.toResult
import com.ly.lib.mvvm.domain.entity.Character
import io.reactivex.Observable

class LocalCharacterDataSource : CharacterDataSource {

    private var data: List<Character> = emptyList()

    override fun getCharacters(type: String): Observable<Result<List<Character>>> {
        return Observable.create<List<Character>> { emitter ->
                if (data.size >= 0) {
                    emitter.onNext(data)
                }
                emitter.onComplete()
            }
            .map<Result<List<Character>>> { it.toResult() }
            .onErrorReturn { it.toErrorResult() }
    }

    fun saveCharacters(list: List<Character>) {
        data = list
    }

}