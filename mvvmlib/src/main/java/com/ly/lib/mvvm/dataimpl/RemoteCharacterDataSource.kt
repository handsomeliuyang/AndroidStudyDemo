package com.ly.lib.mvvm.dataimpl

import com.ly.lib.mvvm.domain.data.CharacterDataSource
import com.ly.lib.mvvm.domain.Result
import com.ly.lib.mvvm.domain.toErrorResult
import com.ly.lib.mvvm.domain.toResult
import com.ly.lib.mvvm.domain.entity.Character
import io.reactivex.Observable

class RemoteCharacterDataSource : CharacterDataSource {

    override fun getCharacters(type: String): Observable<Result<List<Character>>> {
        return Observable.create<List<Character>> {emitter ->
                val list = NetworkModule.service.getCharacters(type)
                    .execute()
                    .body() ?: emptyList()
                emitter.onNext(list)
                emitter.onComplete()
            }
            .map<Result<List<Character>>> { it.toResult() }
            .onErrorReturn { it.toErrorResult() }
    }

}