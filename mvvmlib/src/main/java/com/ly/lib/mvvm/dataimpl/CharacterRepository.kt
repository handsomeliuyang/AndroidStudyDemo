package com.ly.lib.mvvm.dataimpl

import com.ly.lib.mvvm.domain.data.CharacterDataSource
import com.ly.lib.mvvm.domain.*
import com.ly.lib.mvvm.domain.entity.Character
import io.reactivex.Observable

class CharacterRepository(
    private val localDataSource: LocalCharacterDataSource,
    private val remoteDataSource: RemoteCharacterDataSource
) : CharacterDataSource {

    override fun getCharacters(type: String): Observable<Result<List<Character>>> {
        return Observable.concat(localDataSource.getCharacters(type), getRemoteCharacters(type))
            .onErrorReturn { it.toErrorResult() }
    }

    private fun getRemoteCharacters(type: String): Observable<Result<List<Character>>> {
        return remoteDataSource.getCharacters(type)
            .concatMap { it.toDataObservable() } // 只获取成功的结果，错误结果直接报出来
            .doOnNext { localDataSource.saveCharacters(it) } // 缓存数据，不管是否成功
            .map<Result<List<Character>>> { it.toResult() } // 包装结果返回
            .onErrorReturn { it.toErrorResult() } // 包装错误返回
    }

    companion object {
        private var INSTANCE: CharacterRepository? = null
        fun getInstance():CharacterRepository {
            return INSTANCE ?: CharacterRepository(LocalCharacterDataSource(), RemoteCharacterDataSource())
        }
    }

}