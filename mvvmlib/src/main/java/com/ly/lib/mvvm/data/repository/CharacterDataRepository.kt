package com.ly.lib.mvvm.data.repository

import android.util.Log
import com.ly.lib.mvvm.data.repository.datasource.LocalCharacterDataSource
import com.ly.lib.mvvm.data.repository.datasource.RemoteCharacterDataSource
import com.ly.lib.mvvm.domain.entity.Character
import com.ly.lib.mvvm.domain.repository.CharacterRepository
import com.ly.lib.mvvm.domain.usecase.Result
import com.ly.lib.mvvm.domain.usecase.toDataObservable
import com.ly.lib.mvvm.domain.usecase.toErrorResult
import com.ly.lib.mvvm.domain.usecase.toResult
import io.reactivex.Observable

class CharacterDataRepository(
    private val localDataSource: LocalCharacterDataSource,
    private val remoteDataSource: RemoteCharacterDataSource
) : CharacterRepository {

    override fun getCharacters(type: String): Observable<Result<List<Character>>> {
        return Observable.concat(localDataSource.getCharacters(type), getRemoteCharacters(type))
            .onErrorReturn { it.toErrorResult() }
    }

    private fun getRemoteCharacters(type: String): Observable<Result<List<Character>>> {
        return remoteDataSource.getCharacters(type)
            .concatMap { it.toDataObservable() } // 只获取成功的结果，错误结果直接报出来
            .doOnNext {
                Log.d("liuyang", "save data to local ${it}")
                localDataSource.saveCharacters(it)
            } // 缓存数据，不管是否成功
            .map<Result<List<Character>>> { it.toResult() } // 包装结果返回
            .onErrorReturn { it.toErrorResult() } // 包装错误返回
    }

    companion object {
        val INSTANCE: CharacterDataRepository by lazy {
            CharacterDataRepository(
                LocalCharacterDataSource(),
                RemoteCharacterDataSource()
            )
        }
    }

}