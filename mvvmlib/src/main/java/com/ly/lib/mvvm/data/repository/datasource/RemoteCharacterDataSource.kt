package com.ly.lib.mvvm.data.repository.datasource

import com.ly.lib.mvvm.data.net.NetworkModule
import com.ly.lib.mvvm.domain.usecase.Result
import com.ly.lib.mvvm.domain.usecase.toErrorResult
import com.ly.lib.mvvm.domain.usecase.toResult
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