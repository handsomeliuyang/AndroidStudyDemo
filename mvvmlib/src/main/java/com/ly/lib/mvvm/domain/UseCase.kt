package com.ly.lib.mvvm.domain

import io.reactivex.Observable

abstract class UseCase<T: UseCase.RequestValues, R> {

    abstract fun execute(requestValues: T?): Observable<Result<R>>

    interface RequestValues
}

sealed class Result<T> {
    data class Success<T>(val data: T): Result<T>()
    data class Error<T>(val message: String, val error: Throwable?=null): Result<T>()
}

fun <T> Result<T>.toObservable(): Observable<Result<T>> {
    return when (this) {
        is Result.Success -> Observable.just(this)
        is Result.Error -> Observable.error(this.error)
    }
}

fun <T> Result<T>.toDataObservable(): Observable<T> {
    return when(this){
        is Result.Success -> Observable.just(this.data)
        is Result.Error -> Observable.error(this.error)
    }
}

fun <T> Throwable.toErrorResult(): Result<T> {
    return Result.Error(this.message?:"", this)
}

fun <T> T.toResult(): Result<T> {
    return Result.Success(this)
}

