package com.ly.lib.mvvm.domain.usecase

import com.ly.lib.mvvm.domain.executor.PostExecutionThread
import com.ly.lib.mvvm.domain.executor.ThreadExecutor
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

abstract class UseCase<T: UseCase.RequestValues, R>(
    private val threadExecutor: ThreadExecutor,
    private val postExecutionThread: PostExecutionThread
) {

    private val _disposables: CompositeDisposable by lazy { CompositeDisposable() }

    abstract fun buildUseCaseObservable(requestValues: T?): Observable<Result<R>>

    fun execute(requestValues: T?, observer: DisposableObserver<Result<R>>) {
        val disposable = this.buildUseCaseObservable(requestValues)
            .subscribeOn(Schedulers.from(threadExecutor))
            .observeOn(postExecutionThread.getScheduler())
            .subscribeWith(observer)

        _disposables.add(disposable)
    }

    fun dispose(){
        if(_disposables.isDisposed) {
            return
        }
        _disposables.dispose()
    }

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
    return Result.Error(this.message ?: "", this)
}

fun <T> T.toResult(): Result<T> {
    return Result.Success(this)
}

